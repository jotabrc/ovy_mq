package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.Client;

import java.util.List;

public interface ConsumerRegistry {

    void updateClientList(Client client);
    void remove(String clientId);
    Client findConsumerByClientId(String clientId);
    Client findLeastRecentlyUsedConsumerAvailableForTopic(String topic);
    List<Client> findOneAvailableConsumerPerTopic();
    List<Client> findAllAvailableConsumers();
    Integer isThereAnyAvailableConsumerForTopic(String topic);
}