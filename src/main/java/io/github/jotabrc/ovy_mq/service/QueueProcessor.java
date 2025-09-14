package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.Consumer;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;

import java.util.List;

public interface QueueProcessor {

    void save(MessagePayload messagePayload);
    void send(Consumer consumer);
    void send(String clientId);
    void send(Consumer consumer, MessagePayload messagePayload);
    List<MessagePayload> getMessagesByTopic(String topic);
    List<MessagePayload> getMessagesByTopic(String topic, int quantity);
    void remove(MessagePayload message);
}
