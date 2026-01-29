package io.github.jotabrc.ovy_mq_core.components.factories.impl;

import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.ClientFactoryStrategy;
import io.github.jotabrc.ovy_mq_core.components.util.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import org.springframework.stereotype.Component;

@Component
public class ClientBasicFactoryStrategy implements ClientFactoryStrategy {

    public Client create(DefinitionMap definition) {
        return Client.builder()
                .id(definition.extract(OvyMqConstants.CLIENT_ID, String.class))
                .topic(definition.extract(OvyMqConstants.SUBSCRIBED_TOPIC, String.class))
                .build();
    }

    @Override
    public ClientType supports() {
        return ClientType.CONSUMER_MESSAGE_REQUEST_BASIC;
    }
}
