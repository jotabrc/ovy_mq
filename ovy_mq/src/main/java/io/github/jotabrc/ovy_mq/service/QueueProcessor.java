package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;

import java.util.List;

public interface QueueProcessor {

    void save(MessagePayload messagePayload);
    void send(Client client);
    void send(String clientId);
    void send(Client client, MessagePayload messagePayload);
    List<MessagePayload> getMessageByTopic(String topic);
    List<MessagePayload> getMessageByTopic(String topic, int quantity);
    void remove(MessagePayload message);
}
