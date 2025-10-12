package io.github.jotabrc.ovy_mq_client.domain.factory;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.ContentRecord;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;

public class ContentRecordFactory {

    private ContentRecordFactory() {}

    public static ContentRecord of(Client client, MessagePayload messagePayload) {
        return ContentRecord.builder()
                .client(client)
                .messagePayload(messagePayload)
                .build();
    }
}
