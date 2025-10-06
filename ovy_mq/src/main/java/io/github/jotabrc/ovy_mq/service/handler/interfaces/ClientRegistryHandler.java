package io.github.jotabrc.ovy_mq.service.handler.interfaces;

import io.github.jotabrc.ovy_mq.domain.Client;

import java.util.List;

public interface ClientRegistryHandler extends AbstractHandler {

    void updateClientList(Client client);
    void remove(String clientId);
    Client findConsumerByClientId(String clientId);
    Client findLeastRecentlyUsedConsumerAvailableForTopic(String topic);
    List<Client> findOneAvailableConsumerPerTopic();
    List<Client> findAllAvailableConsumers();
    Integer isThereAnyAvailableConsumerForTopic(String topic);
}