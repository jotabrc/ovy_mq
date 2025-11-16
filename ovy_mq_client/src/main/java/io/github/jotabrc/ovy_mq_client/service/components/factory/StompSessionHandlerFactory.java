package io.github.jotabrc.ovy_mq_client.service.components.factory;

import io.github.jotabrc.ovy_mq_client.service.components.handler.StompSessionHandler;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.factories.interfaces.AbstractFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.*;

@RequiredArgsConstructor
@Component
public class StompSessionHandlerFactory implements AbstractFactory<StompSessionHandler> {

    private final ObjectProvider<StompSessionHandler> provider;

    @Override
    public StompSessionHandler create(Map<String, Object> definitions) {
        StompSessionHandler sessionManager = provider.getObject();
        sessionManager.setClient(Key.extract(definitions, Key.FACTORY_CLIENT_OBJECT, Client.class));
        sessionManager.setSubscriptions(Key.extractToList(definitions, Key.FACTORY_SUBSCRIPTIONS, String.class));
        return sessionManager;
    }

    @Override
    public Class<StompSessionHandler> supports() {
        return StompSessionHandler.class;
    }

    private List<String> create(String topic) {
        return List.of(WS_USER + WS_HEALTH,
                WS_USER + WS_QUEUE + "/%s".formatted(topic));
    }
}
