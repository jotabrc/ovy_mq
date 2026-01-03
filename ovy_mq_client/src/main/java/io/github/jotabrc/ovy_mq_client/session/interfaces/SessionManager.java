package io.github.jotabrc.ovy_mq_client.session.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.client.Client;

import java.util.List;

public interface SessionManager {

    SessionManager send(String destination, Object payload);
    void initializeManagers();
    void initializeSession();
    boolean isConnected();
    boolean disconnect(boolean force);
    void defineMembers(Client client, List<String> subscriptions);
    String getClientId();
    boolean destroy(boolean force);
}
