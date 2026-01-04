package io.github.jotabrc.ovy_mq_client.messaging.payload.handler;

import io.github.jotabrc.ovy_mq_client.messaging.payload.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ListenerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ListenerConfigHandler implements PayloadHandler<ListenerConfig> {

    private final Scale scaleComponent;

    @Override
    public void handle(Client client, ListenerConfig payload, StompHeaders headers) {
        scaleComponent.scale(payload);
    }

    @Override
    public Class<ListenerConfig> supports() {
        return ListenerConfig.class;
    }
}