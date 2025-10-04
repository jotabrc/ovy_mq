package io.github.jotabrc.ovy_mq_client.service.domain.client.strategy;

import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientMessageStrategy {

    private final ClientMessageHandler messageHandler;

    public ClientMessageHandler get() {
        return messageHandler;
    }
}
