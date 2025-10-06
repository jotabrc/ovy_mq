package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.service.handler.interfaces.AbstractHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServerHandler {

    CLIENT_REGISTRY(ClientRegistryHandler.class),
    MESSAGE_HANDLER(MessageHandler.class),
    QUEUE_HANDLER(QueueHandler.class);

    private final Class<? extends AbstractHandler> handler;
}
