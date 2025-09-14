package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.Consumer;

import java.util.List;

public interface ConsumerRegistry {

    void updateClientList(Consumer consumer);
    void remove(String clientId);
    Consumer findConsumerByClientId(String clientId);
    Consumer findLeastRecentlyUsedConsumerAvailableForTopic(String topic);
    List<Consumer> findOneAvailableConsumersPerTopic();
    List<Consumer> findAllAvailableConsumers();
    Integer isThereAnyAvailableConsumerForTopic(String topic);
}