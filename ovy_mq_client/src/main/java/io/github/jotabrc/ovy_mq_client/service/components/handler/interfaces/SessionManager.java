package io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.Client;

public interface SessionManager {

    SessionManager send(String destination, Object payload);
    void initialize();
    SessionManager reconnectIfNotAlive(boolean force);
    boolean isConnected();
    void setClient(Client client);
}
