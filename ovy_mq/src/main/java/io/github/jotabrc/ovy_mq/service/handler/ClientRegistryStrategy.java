package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.service.ApplicationContextHolder;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryAbstractHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryRemoveHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistrySelectHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryUpsertHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClientRegistryStrategy {

    UPSERT(ClientRegistryUpsertHandler.class),
    SELECT(ClientRegistrySelectHandler.class),
    REMOVE(ClientRegistryRemoveHandler.class);

    private final Class<? extends ClientRegistryAbstractHandler> beanClass;

    public ClientRegistryAbstractHandler getHandler() {
        return ApplicationContextHolder.get().getBean(this.beanClass);
    }
}
