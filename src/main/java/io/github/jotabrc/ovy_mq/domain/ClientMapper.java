package io.github.jotabrc.ovy_mq.domain;

import java.time.OffsetDateTime;

public class ClientMapper {

    private ClientMapper() {}

    public static Consumer of(String clientId, String topic) {
        return Consumer.builder()
                .id(clientId)
                .listeningTopic(topic)
                .isAvailable(true)
                .lastUsed(OffsetDateTime.now())
                .build();
    }
}
