package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.Consumer;

import java.util.List;

public interface ConsumerRegistry {

    void updateClientList(Consumer consumer);
    void remove(String clientId);
    Consumer getConsumerByClientId(String clientId);
    Consumer obtainLeastRecentlyUsedConsumerAvailable(String topic);
    List<Consumer> getOneAvailableConsumerPerTopic();
    List<Consumer> getAvailableConsumers();
    Integer getAvailableConsumersForTopic(String topic);
}
