package io.github.jotabrc.ovy_mq_client.factory.header;

import io.github.jotabrc.ovy_mq_client.session.stomp.StompSessionHandler;
import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
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
        sessionManager.setClient(definition.extract(OvyMqConstants.CLIENT_OBJECT, Client.class));
        sessionManager.setSubscriptions(definition.extractToList(OvyMqConstants.SUBSCRIPTIONS, String.class));
        return sessionManager;
    }

    @Override
    public Class<StompSessionHandler> supports() {
        return StompSessionHandler.class;
    }
}
