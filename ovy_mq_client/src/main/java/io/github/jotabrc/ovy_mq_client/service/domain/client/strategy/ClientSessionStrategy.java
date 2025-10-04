package io.github.jotabrc.ovy_mq_client.service.domain.client.strategy;

import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientSessionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientSessionStrategy {

    private final ClientSessionHandler sessionHandler;

    public ClientSessionHandler get() {
        return sessionHandler;
    }
}
