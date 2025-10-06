package io.github.jotabrc.ovy_mq.service.handler.interfaces;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;

public interface MessageHandler extends AbstractHandler {

    void process(MessagePayload message);
    void removeFromProcessingQueue(MessagePayload message);
}
