package io.github.jotabrc.ovy_mq_client.service.handler.payload.interfaces;

import org.springframework.messaging.simp.stomp.StompHeaders;

public interface PayloadHandler<T> {

    void handle(String clientId, T payload, StompHeaders headers);
    Class<T> supports();
}
