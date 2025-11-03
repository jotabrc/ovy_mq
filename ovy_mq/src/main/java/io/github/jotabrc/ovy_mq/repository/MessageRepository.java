package io.github.jotabrc.ovy_mq.repository;

import io.github.jotabrc.ovy_mq_core.domain.MessagePayload;

import java.util.List;

public interface MessageRepository {

    MessagePayload saveToQueue(MessagePayload messagePayload);
    MessagePayload pollFromQueue(String topic);
    List<MessagePayload> getMessagesByLastUsedDateGreaterThen(Long ms);
    void removeFromQueue(String topic, String messageId);
    void removeAndRequeue(MessagePayload messagePayload);
}
