package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.service.components.handler.ConfigurerStompSessionHandler;
import io.github.jotabrc.ovy_mq_client.service.components.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class StompSessionHandlerAdapterFactory implements AbstractFactory<ConfigurerStompSessionHandler, Client> {

    private final ObjectProvider<ConfigurerStompSessionHandler> provider;

    @Override
    public ConfigurerStompSessionHandler create(Map<String, Client> definitions) {
        ConfigurerStompSessionHandler sessionManager = provider.getObject();
        sessionManager.setClient(definitions.getOrDefault(Key.FACTORY_CLIENT, null));
        return sessionManager;
    }

    @Override
    public Class<ConfigurerStompSessionHandler> supports() {
        return ConfigurerStompSessionHandler.class;
    }
}
