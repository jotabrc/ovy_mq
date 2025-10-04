package io.github.jotabrc.ovy_mq_client.service.domain.client.interfaces;

import io.github.jotabrc.ovy_mq_client.service.domain.client.ClientSession;

public interface ClientSessionHandler extends AbstractHandler {

    void putIfAbsent(String topic, ClientSession clientSession);
    void requestMessage(String topic, Object object);
}
