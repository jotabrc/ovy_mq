package io.github.jotabrc.ovy_mq_client.service.domain.client;

import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.service.domain.ClientContextHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Aspect
@Component
public class ListenerInterceptor {

    private final ObjectProvider<ClientContextHolder> clientContextHolder;

    @Around("@annotation(ovyListener)")
    public void consumerProcessingAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Executing listener Aspect");

        Object[] args = joinPoint.getArgs();
        MessagePayload messagePayload = (MessagePayload) args[0];
        Object[] newArgs = new Object[]{messagePayload.getPayload()};

        try {
            joinPoint.proceed(newArgs);
            messagePayload.cleanDataAndUpdateSuccessValue(true);
        } catch (Exception e) {
            messagePayload.cleanDataAndUpdateSuccessValue(false);
        } finally {
            clientContextHolder.getObject().getClient().requestMessage();
        }
    }
}
