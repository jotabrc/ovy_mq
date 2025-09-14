package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.Client;

public interface ConsumerRegistry {

    boolean updateClientList(Client client);
    boolean remove(String clientId);
    Client obtainConsumerAvailableInOrderOfOlderUsedFirst(String topic);
}
