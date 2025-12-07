package io.github.jotabrc.ovy_mq_core.components.factories;

import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProducerClientFactoryStrategy implements io.github.jotabrc.ovy_mq_core.components.factories.interfaces.ClientFactoryStrategy {

    public Client create(DefinitionMap definition) {
        return Client.builder()
                .id(definition.extractOrGet(Key.HEADER_CLIENT_ID, UUID.randomUUID().toString()))
                .topic(definition.extract(Key.HEADER_TOPIC, String.class))
                .type(definition.extract(Key.HEADER_CLIENT_TYPE, ClientType.class))
                .build();
    }

    @Override
    public ClientType supports() {
        return ClientType.PRODUCER;
    }
}
