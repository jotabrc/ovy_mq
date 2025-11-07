package io.github.jotabrc.ovy_mq_client.service.components.handler.payload.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.Client;
import org.springframework.messaging.simp.stomp.StompHeaders;

public interface PayloadHandler<T> {

    void handle(Client client, T payload, StompHeaders headers);
    Class<T> supports();
}
