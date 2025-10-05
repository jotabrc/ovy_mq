package io.github.jotabrc.ovy_mq_client.domain;

import io.github.jotabrc.ovy_mq_client.service.domain.client.ClientSession;

import java.lang.reflect.Method;

public class ClientFactory {

    private ClientFactory() {}

    public static Client createConsumer(String topic, ClientSession clientSession, Method method) {
        return Client.builder()
                .topic(topic)
                .method(method)
                .clientSession(clientSession)
                .isAvailable(true)
                .build();
    }

    public static Client createConsumer(String topic, Method method) {
        return Client.builder()
                .topic(topic)
                .method(method)
                .isAvailable(true)
                .build();
    }

    public static Client createConsumer(String topic, ClientSession clientSession) {
        return Client.builder()
                .topic(topic)
                .clientSession(clientSession)
                .isAvailable(true)
                .build();
    }

    public static Client createConsumer(String topic) {
        return Client.builder()
                .topic(topic)
                .isAvailable(true)
                .build();
    }
}
