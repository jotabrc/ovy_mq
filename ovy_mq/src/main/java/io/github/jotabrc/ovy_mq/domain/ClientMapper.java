package io.github.jotabrc.ovy_mq.domain;

public class ClientMapper {

    private ClientMapper() {}

    public static Client of(String clientId, String topic) {
        return Client.builder()
                .id(clientId)
                .topic(topic)
                .isAvailable(true)
                .build();
    }
}
