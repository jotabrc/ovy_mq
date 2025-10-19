package io.github.jotabrc.ovy_mq_client.service.processor.interfaces;

import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;

public interface ClientMessageHandler {

    void handle(String clientId, String topic, MessagePayload messagePayload);
}
