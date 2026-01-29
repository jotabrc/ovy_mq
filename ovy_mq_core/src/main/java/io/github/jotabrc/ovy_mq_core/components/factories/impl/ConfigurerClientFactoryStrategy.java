package io.github.jotabrc.ovy_mq_core.components.factories.impl;

import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.ClientFactoryStrategy;
import io.github.jotabrc.ovy_mq_core.components.util.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ConfigurerClientFactoryStrategy implements ClientFactoryStrategy {

    public Client create(DefinitionMap definition) {
        return Client.builder()
                .id(definition.extractOrGet(OvyMqConstants.CLIENT_ID, UUID.randomUUID().toString()))
                .topic(OvyMqConstants.ROLE_CONFIGURER)
                .type(definition.extract(OvyMqConstants.CLIENT_TYPE, ClientType.class))
                .build();
    }

    @Override
    public ClientType supports() {
        return ClientType.CONFIGURER;
    }
}
