package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.*;
import io.github.jotabrc.ovy_mq_client.util.ApplicationContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

public enum ClientHandler {

    @Autowired

    LISTENER_INITIALIZER(ClientListenerHandler.class),
    SESSION_INITIALIZER(ClientSessionInitializerHandler.class),
    CLIENT_HANDLER(ClientRegistryHandler.class),
    CLIENT_MESSAGE(ClientMessageHandler.class);

    private final Class<? extends AbstractHandler> handler;

    ClientHandler(Class<? extends AbstractHandler> handler) {
        this.handler = handler;
    }

    public AbstractHandler getHandler() {
        return ApplicationContextHolder.getBean(this.handler);
    }
}
