package io.github.jotabrc.ovy_mq_client.component.listener;

import io.github.jotabrc.ovy_mq_client.component.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_core.domain.client.OvyListener;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.REQUEST_MESSAGE;
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
            log.info("Executing client: client={} topic={}", client.getId(), client.getTopic());
        }

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw new OvyException.ListenerExecution("Error while executing listener: client=%s topic=%s".formatted(client.getId(), client.getTopic()));
        } finally {
            if (nonNull(client)) client.setIsAvailable(true);
            if (!client.getIsDestroying()) {
                client.setIsMessageInteractionActive(true);
                clientMessageDispatcher.send(client, client.getTopic(), REQUEST_MESSAGE, client.getTopic());
            }
        }
    }
}
