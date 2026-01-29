package io.github.jotabrc.ovy_mq_core.components.factories.impl;

import io.github.jotabrc.ovy_mq_core.components.factories.ClientFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ClientFactory implements AbstractFactory<Client> {

    private final ClientFactoryResolver clientFactoryResolver;

    @Override
    public Client create(DefinitionMap definition) {
        return clientFactoryResolver.create(definition);
    }

    @Override
    public Class<Client> supports() {
        return Client.class;
    }
}
