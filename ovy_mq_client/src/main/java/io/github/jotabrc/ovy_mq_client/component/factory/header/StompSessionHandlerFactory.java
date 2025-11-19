package io.github.jotabrc.ovy_mq_client.component.factory.header;

import io.github.jotabrc.ovy_mq_client.component.session.StompSessionHandler;
import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StompSessionHandlerFactory implements AbstractFactory<StompSessionHandler> {

    private final ObjectProvider<StompSessionHandler> provider;

    @Override
    public StompSessionHandler create(DefinitionMap definition) {
        StompSessionHandler sessionManager = provider.getObject();
        sessionManager.setClient(definition.extract(Key.FACTORY_CLIENT_OBJECT, Client.class));
        sessionManager.setSubscriptions(definition.extractToList(Key.FACTORY_SUBSCRIPTIONS, String.class));
        return sessionManager;
    }

    @Override
    public Class<StompSessionHandler> supports() {
        return StompSessionHandler.class;
    }
}
