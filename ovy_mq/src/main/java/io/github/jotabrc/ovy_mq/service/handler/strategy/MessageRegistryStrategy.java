package io.github.jotabrc.ovy_mq.service.handler.strategy;

import io.github.jotabrc.ovy_mq.service.ApplicationContextHolder;
import io.github.jotabrc.ovy_mq.service.handler.MessageRemoveHandler;
import io.github.jotabrc.ovy_mq.service.handler.MessageRequestHandler;
import io.github.jotabrc.ovy_mq.service.handler.MessageSaveHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MessageRegistryStrategy {

    SAVE(MessageSaveHandler.class),
    REQUEST(MessageRequestHandler.class),
    REMOVE(MessageRemoveHandler.class);

    private final Class<? extends MessageHandler> beanClass;

    public MessageHandler getHandler() {
        return ApplicationContextHolder.get().getBean(this.beanClass);
    }
}
