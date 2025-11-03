package io.github.jotabrc.ovy_mq_client.service.handler.interfaces;


import io.github.jotabrc.ovy_mq_core.domain.Client;

public interface ClientSessionInitializerHandler {

    void initialize(Client client);
}
