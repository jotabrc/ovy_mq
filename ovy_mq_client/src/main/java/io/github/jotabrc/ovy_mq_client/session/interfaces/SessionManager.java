package io.github.jotabrc.ovy_mq_client.session.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.client.Client;

import java.util.List;

public interface SessionManager {

    void defineMembers(Client client, List<String> subscriptions);
    String getClientId();
}
