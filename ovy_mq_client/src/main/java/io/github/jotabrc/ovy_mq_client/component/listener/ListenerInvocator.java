package io.github.jotabrc.ovy_mq_client.component.listener;

import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ListenerInvocator {

    private final ListenerExecutionContextHolder listenerExecutionContextHolder;
    private final ApplicationContext applicationContext;

    public void invoke(Client client, Object payload) {
        try {
            client.setIsAvailable(false);
            listenerExecutionContextHolder.setThreadLocal(client);
            Object bean = applicationContext.getBean(client.getBeanName());
            AopUtils.invokeJoinpointUsingReflection(bean, client.getMethod(), new Object[]{payload});
        } catch (Throwable e) {
            throw new OvyException.ListenerExecution("Error while invoking listener: client=%s topic=%s".formatted(client.getId(), client.getTopic()));
        } finally {
            client.setIsAvailable(true);
            listenerExecutionContextHolder.clear();
        }
    }
}
