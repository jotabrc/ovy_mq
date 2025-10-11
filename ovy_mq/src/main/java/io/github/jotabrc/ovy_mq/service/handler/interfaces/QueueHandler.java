package io.github.jotabrc.ovy_mq.service.handler.interfaces;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;

import java.util.List;

public interface QueueHandler extends AbstractHandler {

    void save(MessagePayload messagePayload);
    void send(Client client);
    void send(String clientId);
    void send(Client client, MessagePayload messagePayload);
    List<MessagePayload> getMessageByTopic(String topic);
    List<MessagePayload> getMessageByTopic(String topic, int quantity);
    void remove(String topic, String messageId);
}
