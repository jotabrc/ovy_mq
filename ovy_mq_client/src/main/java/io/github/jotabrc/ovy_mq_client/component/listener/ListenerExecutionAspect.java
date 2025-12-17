package io.github.jotabrc.ovy_mq_client.component.listener;

import io.github.jotabrc.ovy_mq_client.component.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.OvyListener;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.REQUEST_MESSAGE;
import static java.util.Objects.nonNull;

@Aspect
@Slf4j
@RequiredArgsConstructor
@Component
public class ListenerExecutionAspect {

    @Value("ovymq.client.processing.max-retries:3")
    private int maxRetries;

    @Value("ovymq.client.processing.exponential-timer:1000")
    private long exponentialTimer;

    private final ListenerAfterProcessingHandler listenerAfterProcessingHandler;
    private final ListenerExecutionContextHolder listenerExecutionContextHolder;
    private final ClientMessageDispatcher clientMessageDispatcher;
    private final ScheduledExecutorService scheduledBackoffExecutor;

    @Around("@annotation(ovyListener)")
    public Object manageClientAvailability(ProceedingJoinPoint joinPoint, OvyListener ovyListener) {
        Client client = listenerExecutionContextHolder.getClient();
        if (nonNull(client)) {
            log.info("Executing client: client={} topic={}", client.getId(), client.getTopic());
        }

        try {
            AtomicInteger retries = new AtomicInteger(1);
            return backoffExecution(joinPoint, client, retries);
        } catch (Exception e) {
            log.error("Error while executing listener: client={} topic={}", client.getId(), client.getTopic(), e);
            return null;
        }
    }

    private Callable<Object> getCallable(ProceedingJoinPoint joinPoint, Client client, AtomicInteger retries) {
        if (retries.get() > maxRetries) return null;
        return () -> {
            try {
                Object object = joinPoint.proceed();
                updateClient(client);
                listenerAfterProcessingHandler.afterSuccess(joinPoint.getArgs()[0]);
                return object;
            } catch (Throwable e) {
                log.warn("Error while processing message for client={} topic={} retry={}/{}", client.getId(), client.getTopic(), retries, maxRetries);
                retries.incrementAndGet();
                return backoffExecution(joinPoint, client, retries);
            }
        };
    }

    private Object backoffExecution(ProceedingJoinPoint joinPoint, Client client, AtomicInteger retries) {
        try {
            Callable<Object> callable = getCallable(joinPoint, client, retries);
            long executionDelay = Objects.equals(retries.get(), 1)
                    ? 0
                    : exponentialTimer * retries.get() + 1;

            if (nonNull(callable)) {
                return scheduledBackoffExecutor.schedule(callable, executionDelay, TimeUnit.MILLISECONDS);
            } else {
                updateClient(client);
                OvyException.ListenerExecution exception = new OvyException.ListenerExecution("Error while executing listener: client=%s topic=%s after %d retries".formatted(client.getId(), client.getTopic(), maxRetries));
                listenerAfterProcessingHandler.afterFailure(joinPoint.getArgs()[0], exception);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateClient(Client client) {
        if (nonNull(client)) client.setIsAvailable(true);
        if (!client.getIsDestroying()) {
            client.setIsMessageInteractionActive(true);
            clientMessageDispatcher.send(client, client.getTopic(), REQUEST_MESSAGE, client.getTopic());
        }
    }
}
