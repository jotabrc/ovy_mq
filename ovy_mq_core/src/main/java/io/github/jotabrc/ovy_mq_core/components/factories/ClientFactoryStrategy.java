package io.github.jotabrc.ovy_mq_core.components.factories;

import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.client.ListenerConfig;
import io.github.jotabrc.ovy_mq_core.domain.client.OvyListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Component
public class ClientFactoryStrategy implements io.github.jotabrc.ovy_mq_core.components.factories.interfaces.ClientFactoryStrategy {

    public Client create(DefinitionMap definition) {
        OvyListener listener = definition.extract(Key.FACTORY_OVY_LISTENER, OvyListener.class);
        return Client.builder()
                .id(definition.extractOrGet(Key.HEADER_CLIENT_ID, UUID.randomUUID().toString()))
                .topic(listener.topic())
                .method(definition.extract(Key.FACTORY_CLIENT_METHOD, Method.class))
                .beanName(definition.extract(Key.FACTORY_CLIENT_BEAN_NAME, String.class))
                .isAvailable(definition.extract(Key.FACTORY_CLIENT_IS_AVAILABLE, Boolean.class))
                .type(definition.extract(Key.HEADER_CLIENT_TYPE, ClientType.class))
                .config(ListenerConfig.builder()
                        .processingTimeout(listener.processingTimeout())
                        .pollInitialDelay(listener.pollInitialDelay())
                        .pollFixedDelay(listener.pollFixedDelay())
                        .healthCheckInitialDelay(listener.healthCheckInitialDelay())
                        .healthCheckFixedDelay(listener.healthCheckFixedDelay())
                        .healthCheckExpirationTime(listener.healthCheckExpirationTime())
                        .connectionMaxRetries(listener.connectionMaxRetries())
                        .connectionTimeout(listener.connectionTimeout())
                        .useGlobalValues(listener.useGlobalValues())
                        .build())
                .build();
    }

    @Override
    public ClientType supports() {
        return ClientType.CONSUMER;
    }
}
