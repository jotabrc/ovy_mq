package io.github.jotabrc.ovy_mq_client.domain;

public class ActionFactory {

    private ActionFactory() {}

    public static Action of(Client client, MessagePayload messagePayload) {
        return Action.builder()
                .client(client)
                .messagePayload(messagePayload)
                .build();
    }

    public static Action of(Client client) {
        return Action.builder()
                .client(client)
                .build();
    }

    public static Action and() {
        return Action.builder()
                .build();
    }
}
