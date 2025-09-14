package io.github.jotabrc.ovy_mq.repository;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;

public interface MessageRepository {

    void saveToQueue(MessagePayload message);
    MessagePayload removeFromQueueAndReturn(String topic);
}
