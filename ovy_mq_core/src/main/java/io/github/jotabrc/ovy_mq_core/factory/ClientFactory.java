package io.github.jotabrc.ovy_mq_core.factory;

import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ListenerState;

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

    public static Client of(String topic, Method method, String beanName, ListenerState listenerState) {
        return Client.builder()
                .id(UUID.randomUUID().toString())
                .topic(topic)
                .method(method)
                .beanName(beanName)
                .isAvailable(true)
                .listenerState(listenerState)
                .build();
    }
}
