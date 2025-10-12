package io.github.jotabrc.ovy_mq.service.handler.strategy;

import io.github.jotabrc.ovy_mq.service.ApplicationContextHolder;
import io.github.jotabrc.ovy_mq.service.handler.ClientRegistryRemoveHandler;
import io.github.jotabrc.ovy_mq.service.handler.ClientRegistrySelectHandler;
import io.github.jotabrc.ovy_mq.service.handler.ClientRegistryUpsertHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClientRegistryStrategy {

    UPSERT(ClientRegistryUpsertHandler.class),
    SELECT(ClientRegistrySelectHandler.class),
    REMOVE(ClientRegistryRemoveHandler.class);

    private final Class<? extends ClientRegistryHandler> beanClass;

    public ClientRegistryHandler getHandler() {
        return ApplicationContextHolder.get().getBean(this.beanClass);
    }
}
