package io.github.jotabrc.ovy_mq_core.components.factories.impl.strategy;

import io.github.jotabrc.ovy_mq_core.components.util.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.*;
import io.github.jotabrc.ovy_mq_core.domain.client.annotation.OvyListener;
import io.github.jotabrc.ovy_mq_core.domain.client.listener_config.ListenerConfig;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Component
public class DefaultClientFactoryStrategy implements io.github.jotabrc.ovy_mq_core.components.factories.interfaces.ClientFactoryStrategy {

    public Client create(DefinitionMap definition) {
        OvyListener listener = definition.extract(OvyMqConstants.OVY_LISTENER, OvyListener.class);
        return Client.builder()
                .id(definition.extractOrGet(OvyMqConstants.CLIENT_ID, UUID.randomUUID().toString()))
                .topic(listener.topic())
                .type(definition.extract(OvyMqConstants.CLIENT_TYPE, ClientType.class))
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
                .execution(ClientExecution.builder()
                        .method(definition.extract(OvyMqConstants.CLIENT_METHOD, Method.class))
                        .beanName(definition.extract(OvyMqConstants.CLIENT_BEAN_NAME, String.class))
                        .build())
                .build();
    }

    @Override
    public ClientType supports() {
        return ClientType.CONSUMER;
    }
}
