package io.github.jotabrc.ovy_mq_client.service.domain.client.strategy;

import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientListenerHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class ClientListenerStrategy {

    private final ClientListenerHandler clientListenerHandler;

    public ClientListenerHandler get() {
        return clientListenerHandler;
    }
}
