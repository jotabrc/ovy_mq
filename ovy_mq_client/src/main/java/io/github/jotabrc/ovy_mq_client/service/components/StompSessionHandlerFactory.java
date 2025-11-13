package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.service.components.handler.ConsumerStompSessionHandler;
import io.github.jotabrc.ovy_mq_client.service.components.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class StompSessionHandlerFactory implements AbstractFactory<ConsumerStompSessionHandler, Client> {

    private final ObjectProvider<ConsumerStompSessionHandler> provider;

    @Override
    public ConsumerStompSessionHandler create(Map<String, Client> definitions) {
        ConsumerStompSessionHandler sessionManager = provider.getObject();
        sessionManager.setClient(definitions.getOrDefault(Key.FACTORY_CLIENT, null));
        return sessionManager;
    }

    @Override
    public Class<ConsumerStompSessionHandler> supports() {
        return ConsumerStompSessionHandler.class;
    }
}
