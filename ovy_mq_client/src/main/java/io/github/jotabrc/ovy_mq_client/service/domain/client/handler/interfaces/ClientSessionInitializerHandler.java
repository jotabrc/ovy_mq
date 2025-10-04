package io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces;

import io.github.jotabrc.ovy_mq_client.domain.Client;

public interface ClientSessionInitializerHandler extends AbstractHandler {

    void initializeSession(Client client);
}
