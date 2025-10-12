package io.github.jotabrc.ovy_mq.domain.factory;


import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.ContentRecord;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;

public class ContentRecordFactory {

    private ContentRecordFactory() {}

    public static ContentRecord of(Client client, MessagePayload messagePayload) {
        return ContentRecord.builder()
                .client(client)
                .messagePayload(messagePayload)
                .build();
    }
}
