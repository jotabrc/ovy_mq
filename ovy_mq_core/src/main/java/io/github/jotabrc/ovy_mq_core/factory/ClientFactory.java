package io.github.jotabrc.ovy_mq_core.factory;

import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;

import java.lang.reflect.Method;
import java.util.UUID;

public class ClientFactory {

    private ClientFactory() {}

    public static Client of(String clientId, String topic) {
        return Client.builder()
                .id(clientId)
                .topic(topic)
                .build();
    }

    public static Client of(String topic, Method method) {
        return Client.builder()
                .id(UUID.randomUUID().toString())
                .topic(topic)
                .method(method)
                .isAvailable(true)
                .build();
    }

    public static Client of(String topic, Method method, String beanName, Long timeout, ClientType type) {
        return Client.builder()
                .id(UUID.randomUUID().toString())
                .topic(topic)
                .method(method)
                .beanName(beanName)
                .timeout(timeout)
                .isAvailable(true)
                .type(type)
                .build();
    }
}
