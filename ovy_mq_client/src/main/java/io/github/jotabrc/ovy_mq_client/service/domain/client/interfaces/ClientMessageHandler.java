package io.github.jotabrc.ovy_mq_client.service.domain.client.interfaces;

import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;

public interface ClientMessageHandler extends AbstractHandler {

    void handleMessage(String topic, MessagePayload object);
}
