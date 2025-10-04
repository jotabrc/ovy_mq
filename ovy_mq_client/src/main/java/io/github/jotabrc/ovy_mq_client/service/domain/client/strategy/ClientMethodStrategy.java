package io.github.jotabrc.ovy_mq_client.service.domain.client.strategy;

import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMethodHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientMethodStrategy {

    private final ClientMethodHandler methodHandler;

    public ClientMethodHandler get() {
        return methodHandler;
    }
}
