package io.github.jotabrc.ovy_mq_core.components.factories;

import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ClientFactory implements AbstractFactory<Client> {

    @Override
    public Client create(DefinitionMap definition) {
        return Client.builder()
                .id(definition.extractOrGet(Key.HEADER_CLIENT_ID, UUID.randomUUID().toString()))
                .topic(definition.extract(Key.HEADER_TOPIC, String.class))
                .method(definition.extract(Key.FACTORY_CLIENT_METHOD, Method.class))
                .beanName(definition.extract(Key.FACTORY_CLIENT_BEAN_NAME, String.class))
                .timeout(definition.extract(Key.FACTORY_CLIENT_TIMEOUT, Long.class))
                .isAvailable(definition.extract(Key.FACTORY_CLIENT_IS_AVAILABLE, Boolean.class))
                .type(definition.extract(Key.HEADER_CLIENT_TYPE, ClientType.class))
                .pollInitialDelay(definition.extract(Key.FACTORY_REPLICA_POLL_INITIAL_DELAY, Long.class))
                .pollFixedDelay(definition.extract(Key.FACTORY_REPLICA_POLL_FIXED_DELAY, Long.class))
                .build();
    }

    @Override
    public Class<Client> supports() {
        return Client.class;
    }
}
