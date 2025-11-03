package io.github.jotabrc.ovy_mq_client.service;

import io.github.jotabrc.ovy_mq_core.domain.Client;
import org.springframework.stereotype.Component;

@Component
public class ListenerExecutionContextHolder {

    private static final ThreadLocal<Client> threadLocal = new ThreadLocal<>();

    public void setThreadLocal(Client client) {
        threadLocal.set(client);
    }

    public Client getClient() {
        return threadLocal.get();
    }

    public void clear() {
        threadLocal.remove();
    }
}
