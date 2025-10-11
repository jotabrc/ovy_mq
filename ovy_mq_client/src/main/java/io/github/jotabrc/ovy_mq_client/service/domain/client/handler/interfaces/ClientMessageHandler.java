package io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces;

import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;

public interface ClientMessageHandler extends AbstractHandler {

    void handleMessage(String clientId, String topic, MessagePayload object);
}
