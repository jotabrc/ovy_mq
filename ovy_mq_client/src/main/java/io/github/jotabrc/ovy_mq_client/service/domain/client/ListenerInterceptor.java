package io.github.jotabrc.ovy_mq_client.service.domain.client;

import io.github.jotabrc.ovy_mq_client.service.domain.server.ServerSession;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Aspect
@Component
public class ListenerInterceptor {

    private final ServerSession serverSession;

    @Around("@annotation(ovyListener)")
    public Object consumerProccesingAspect(ProceedingJoinPoint joinPoint, OvyListener ovyListener) throws Throwable {

        try {
            return joinPoint.proceed();
        } finally {
            serverSession.requestMessage(ovyListener.topic());
        }
    }
}
