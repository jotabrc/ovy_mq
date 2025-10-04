package io.github.jotabrc.ovy_mq_client.service.domain.client.strategy;

import io.github.jotabrc.ovy_mq_client.domain.Action;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMessageHandler;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMethodHandler;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientSessionHandler;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientSessionInitializerHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class ClientExecutor {

    private final ClientSessionInitializerHandler sessionInitializerHandler;
    private final ClientSessionHandler sessionHandler;
    private final ClientMessageHandler messageHandler;
    private final ClientMethodHandler clientMethodHandler;

    public void execute(Action action) {

    }
}
