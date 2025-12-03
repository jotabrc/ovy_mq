package io.github.jotabrc.ovy_mq_core.components.factories;

import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ClientFactory implements AbstractFactory<Client> {

    private final ClientFactoryStrategyResolver clientFactoryStrategyResolver;

    @Override
    public Client create(DefinitionMap definition) {
        return clientFactoryStrategyResolver.create(definition);
    }

    @Override
    public Class<Client> supports() {
        return Client.class;
    }
}
