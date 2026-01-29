package io.github.jotabrc.ovy_mq_client.factory.header;

import io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.stomp_handler.StompClientSessionHandler;
import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.components.util.interfaces.DefinitionMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StompSessionHandlerFactory implements AbstractFactory<StompClientSessionHandler> {

    private final ObjectProvider<StompClientSessionHandler> provider;

    @Override
    public StompClientSessionHandler create(DefinitionMap definition) {
        return provider.getObject();
    }

    @Override
    public Class<StompClientSessionHandler> supports() {
        return StompClientSessionHandler.class;
    }
}
