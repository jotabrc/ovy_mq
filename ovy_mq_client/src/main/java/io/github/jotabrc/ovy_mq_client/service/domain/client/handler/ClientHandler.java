package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.*;
import io.github.jotabrc.ovy_mq_client.util.ApplicationContextHolder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClientHandler {

    LISTENER_INITIALIZER(ClientListenerHandler.class),
    SESSION_INITIALIZER(ClientSessionInitializerHandler.class),
    CLIENT_HANDLER(ClientRegistryHandler.class),
    CLIENT_MESSAGE(ClientMessageHandler.class);

    private final Class<? extends AbstractHandler> handler;

    public AbstractHandler getHandler() {
        return ApplicationContextHolder.getBean(this.handler);
    }
}
