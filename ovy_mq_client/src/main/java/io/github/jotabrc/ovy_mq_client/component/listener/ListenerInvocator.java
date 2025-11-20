package io.github.jotabrc.ovy_mq_client.component.listener;

import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import io.github.jotabrc.ovy_mq_core.util.ApplicationContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ListenerInvocator {

    private final ListenerExecutionContextHolder listenerExecutionContextHolder;

    public void invoke(Client client, Object payload) {
        try {
            listenerExecutionContextHolder.setThreadLocal(client);
            client.setIsAvailable(false);
            Object bean = ApplicationContextHolder.getContextBean(client.getBeanName());
            AopUtils.invokeJoinpointUsingReflection(bean, client.getMethod(), new Object[]{payload});
        } catch (Throwable e) {
            client.setIsAvailable(true);
            throw new OvyException.ListenerExecution("Error while invoking listener: client=%s topic=%s".formatted(client.getId(), client.getTopic()));
        } finally {
            listenerExecutionContextHolder.clear();
        }
    }
}
