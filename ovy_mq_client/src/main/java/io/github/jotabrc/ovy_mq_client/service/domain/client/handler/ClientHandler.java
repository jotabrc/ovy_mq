package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.*;
import io.github.jotabrc.ovy_mq_client.util.ApplicationContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

public enum ClientHandler {

    @Autowired

    CLIENT_LISTENER(ClientListenerHandler.class),
    CLIENT_INITIALIZE_SESSION(ClientSessionInitializerHandler.class),
    CLIENT_SESSION(ClientSessionHandler.class),
    CLIENT_MESSAGE(ClientMessageHandler.class),
    CLIENT_METHOD(ClientMethodHandler.class);

    private final Class<? extends AbstractHandler> handler;

    ClientHandler(Class<? extends AbstractHandler> handler) {
        this.handler = handler;
    }

    public AbstractHandler getHandler() {
        return ApplicationContextHolder.getBean(this.handler);
    }
}
