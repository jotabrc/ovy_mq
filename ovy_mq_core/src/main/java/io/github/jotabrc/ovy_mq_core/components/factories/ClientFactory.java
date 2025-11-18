package io.github.jotabrc.ovy_mq_core.components.factories;

import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
public class ClientFactory implements AbstractFactory<Client> {

    @Override
    public Client create(Map<String, Object> definitions) {
        return Client.builder()
                .id(getDefaultOrCreate(definitions, Key.HEADER_CLIENT_ID, UUID.randomUUID().toString()))
                .topic(Key.extract(definitions, Key.HEADER_TOPIC, String.class))
                .method(Key.extract(definitions, Key.FACTORY_CLIENT_METHOD, Method.class))
                .beanName(Key.extract(definitions, Key.FACTORY_CLIENT_BEAN_NAME, String.class))
                .timeout(Key.extract(definitions, Key.FACTORY_CLIENT_TIMEOUT, Long.class))
                .isAvailable(Key.extract(definitions, Key.FACTORY_CLIENT_IS_AVAILABLE, Boolean.class))
                .type(Key.extract(definitions, Key.HEADER_CLIENT_TYPE, ClientType.class))
                .build();
    }

    @Override
    public Class<Client> supports() {
        return Client.class;
    }

    private <T> T getDefaultOrCreate(Map<String, Object> map, String key, T orDefault) {
        var result = Key.extract(map, key, orDefault.getClass());
        return isNull(result)
                ? orDefault
                : (T) result;
    }
}
