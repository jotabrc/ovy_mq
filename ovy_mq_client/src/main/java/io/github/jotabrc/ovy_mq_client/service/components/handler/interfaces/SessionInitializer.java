package io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces;


import io.github.jotabrc.ovy_mq_core.domain.Client;

public interface SessionInitializer {

    void createSessionAndConnect(Client client);
}
