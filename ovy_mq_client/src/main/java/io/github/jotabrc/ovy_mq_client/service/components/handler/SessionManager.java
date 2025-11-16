package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_core.domain.Client;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.util.List;

public abstract class SessionManager extends StompSessionHandlerAdapter {

    public abstract SessionManager send(String destination, Object payload);
    public abstract void initialize();
    public abstract SessionManager reconnectIfNotAlive(boolean force);
    public abstract boolean isConnected();
    public abstract void setClient(Client client);
    public abstract void setSubscriptions(List<String> subscriptions);
}
