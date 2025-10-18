package io.github.jotabrc.ovy_mq_client.domain.factory;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.service.ClientSession;

import java.lang.reflect.Method;
import java.util.UUID;

public class ClientFactory {

    private ClientFactory() {}

    public static Client of(String topic, ClientSession clientSession, Method method) {
        return Client.builder()
                .topic(topic)
                .method(method)
                .clientSession(clientSession)
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

    public static Client of(String topic, ClientSession clientSession) {
        return Client.builder()
                .topic(topic)
                .clientSession(clientSession)
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
