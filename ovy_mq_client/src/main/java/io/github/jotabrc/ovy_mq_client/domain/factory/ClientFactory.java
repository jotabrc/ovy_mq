package io.github.jotabrc.ovy_mq_client.domain.factory;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.service.handler.ClientSessionHandler;

import java.lang.reflect.Method;
import java.util.UUID;

public class ClientFactory {

    private ClientFactory() {}

    public static Client of(String topic, ClientSessionHandler clientSessionHandler, Method method) {
        return Client.builder()
                .topic(topic)
                .method(method)
                .clientSessionHandler(clientSessionHandler)
                .isAvailable(true)
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

    public static Client of(String topic, Method method, Object beanInstance) {
        return Client.builder()
                .id(UUID.randomUUID().toString())
                .topic(topic)
                .method(method)
                .beanInstance(beanInstance)
                .isAvailable(true)
                .build();
    }

    public static Client of(String topic, ClientSessionHandler clientSessionHandler) {
        return Client.builder()
                .topic(topic)
                .clientSessionHandler(clientSessionHandler)
                .isAvailable(true)
                .build();
    }

    public static Client of(String topic) {
        return Client.builder()
                .topic(topic)
                .isAvailable(true)
                .build();
    }
}
