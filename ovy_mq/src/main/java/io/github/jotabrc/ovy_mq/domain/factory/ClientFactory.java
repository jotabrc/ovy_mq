package io.github.jotabrc.ovy_mq.domain.factory;

import io.github.jotabrc.ovy_mq.domain.Client;

public class ClientFactory {

    private ClientFactory() {}

    public static Client of(String clientId, String topic) {
        return Client.builder()
                .id(clientId)
                .topic(topic)
                .isAvailable(true)
                .build();
    }

    public static Client of(String clientId) {
        return Client.builder()
                .id(clientId)
                .build();
    }
}
