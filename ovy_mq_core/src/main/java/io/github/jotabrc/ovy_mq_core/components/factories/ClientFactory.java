package io.github.jotabrc.ovy_mq_core.components.factories;

import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ClientConfig;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
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
                .isAvailable(definition.extract(Key.FACTORY_CLIENT_IS_AVAILABLE, Boolean.class))
                .type(definition.extract(Key.HEADER_CLIENT_TYPE, ClientType.class))
                .config(ClientConfig.builder()
                        .processingTimeout(definition.extract(Key.FACTORY_PROCESSING_TIMEOUT, Long.class))
                        .pollInitialDelay(definition.extract(Key.FACTORY_CLIENT_CONFIG_POLL_INITIAL_DELAY, Long.class))
                        .pollFixedDelay(definition.extract(Key.FACTORY_CLIENT_CONFIG_POLL_FIXED_DELAY, Long.class))
                        .healthCheckInitialDelay(definition.extract(Key.FACTORY_CLIENT_CONFIG_HEALTH_CHECK_INITIAL_DELAY, Long.class))
                        .healthCheckFixedDelay(definition.extract(Key.FACTORY_CLIENT_CONFIG_HEALTH_CHECK_FIXED_DELAY, Long.class))
                        .healthCheckExpirationTime(definition.extract(Key.FACTORY_CLIENT_CONFIG_HEALTH_CHECK_EXPIRATION_TIME, Long.class))
                        .connectionMaxRetries(definition.extract(Key.FACTORY_CLIENT_CONFIG_CONNECTION_MAX_RETRIES, Integer.class))
                        .connectionTimeout(definition.extract(Key.FACTORY_CLIENT_CONFIG_CONNECTION_TIMEOUT, Long.class))
                        .useGlobalValues(definition.extract(Key.FACTORY_CLIENT_CONFIG_USE_GLOBAL_VALUES, Boolean.class))
                        .build())
                .build();
    }

    @Override
    public Class<Client> supports() {
        return Client.class;
    }
}
