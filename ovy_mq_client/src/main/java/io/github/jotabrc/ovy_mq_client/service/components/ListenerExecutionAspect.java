package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.service.ListenerExecutionContextHolder;
import io.github.jotabrc.ovy_mq_client.service.OvyListener;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Aspect
@Slf4j
@RequiredArgsConstructor
@Component
public class ListenerExecutionAspect {

    private final ListenerExecutionContextHolder listenerExecutionContextHolder;
    private final ClientMessageDispatcher clientMessageDispatcher;

    @Around("@annotation(ovyListener)")
    public Object manageClientAvailability(ProceedingJoinPoint joinPoint, OvyListener ovyListener) {
        Client client = listenerExecutionContextHolder.getClient();
        if (nonNull(client)) {
            log.info("Managing client availability: client={} topic={}", client.getId(), client.getTopic());
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            if (nonNull(client)) client.setIsAvailable(true);
            listenerExecutionContextHolder.clear();
            clientMessageDispatcher.send(client, client.getTopic(), client.requestMessage(), client.getTopic());
        }
    }
}
