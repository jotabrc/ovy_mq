package io.github.jotabrc.ovy_mq.domain.factory;


import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.MessageRecord;

public class MessageRecordFactory {

    private MessageRecordFactory() {}

    public static MessageRecord of(Client client, MessagePayload messagePayload) {
        return MessageRecord.builder()
                .client(client)
                .messagePayload(messagePayload)
                .build();
    }

    public static MessageRecord of(Client client) {
        return MessageRecord.builder()
                .client(client)
                .build();
    }

    public static MessageRecord of(MessagePayload messagePayload) {
        return MessageRecord.builder()
                .messagePayload(messagePayload)
                .build();
    }
}
