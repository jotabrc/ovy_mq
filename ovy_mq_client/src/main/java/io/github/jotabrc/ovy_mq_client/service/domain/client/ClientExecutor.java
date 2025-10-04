package io.github.jotabrc.ovy_mq_client.service.domain.client;

import io.github.jotabrc.ovy_mq_client.service.domain.client.interfaces.*;
import io.github.jotabrc.ovy_mq_client.util.ApplicationContextHolder;

public enum ClientExecutor {

    CLIENT_LISTENER(ClientListenerHandler.class),
    CLIENT_INITIALIZE_SESSION(ClientSessionInitializerHandler.class),
    CLIENT_SESSION(ClientSessionHandler.class),
    CLIENT_MESSAGE(ClientMessageHandler.class),
    CLIENT_METHOD(ClientMethodHandler.class);

    private final Class<? extends AbstractHandler> handler;

    ClientExecutor(Class<? extends AbstractHandler> handler) {
        this.handler = handler;
    }

    public AbstractHandler getHandler() {
        return ApplicationContextHolder.getBean(this.handler);
    }
}
