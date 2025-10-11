package io.github.jotabrc.ovy_mq.repository;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;

import java.util.List;

public interface MessageRepository {

    void saveToQueue(MessagePayload message);
    MessagePayload removeFromQueueAndReturn(String topic);
    List<MessagePayload> removeFromQueueAndReturnList(String topic, int quantity);
    void removeFromProcessingQueue(String topic, String messageId);
}
