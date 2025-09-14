package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;

public interface MessageProcessor {

    void process(MessagePayload message);
    void removeFromProcessingQueue(MessagePayload message);
}
