package io.github.jotabrc.ovy_mq_client.component.session.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.Client;

import java.util.List;

public interface SessionManager {

    SessionManager send(String destination, Object payload);
    void initializeHandler();
    void initializeSession();
    boolean isConnected();
    void disconnect();
    void setClient(Client client);
    void setSubscriptions(List<String> subscriptions);
    void destroy();
}
