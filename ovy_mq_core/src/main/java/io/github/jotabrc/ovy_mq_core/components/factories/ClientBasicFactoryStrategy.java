package io.github.jotabrc.ovy_mq_core.components.factories;

import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import org.springframework.stereotype.Component;

@Component
public class ClientBasicFactoryStrategy implements io.github.jotabrc.ovy_mq_core.components.factories.interfaces.ClientFactoryStrategy {

    public Client create(DefinitionMap definition) {
        return Client.builder()
                .id(definition.extract(Key.HEADER_CLIENT_ID, String.class))
                .topic(definition.extract(Key.HEADER_TOPIC, String.class))
                .build();
    }

    @Override
    public ClientType supports() {
        return ClientType.CONSUMER_MESSAGE_REQUEST_BASIC;
    }
}
