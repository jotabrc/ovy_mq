package io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;

public interface ClientRegistryHandler extends AbstractHandler {

    void save(Client client);
    void executeListener(String topic, MessagePayload messagePayload);
    void requestMessagesForAllAvailableClients();
}
