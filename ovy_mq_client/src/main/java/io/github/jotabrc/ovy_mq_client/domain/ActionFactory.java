package io.github.jotabrc.ovy_mq_client.domain;

public class ActionFactory {

    private ActionFactory() {}

    public static Action create(Client client, MessagePayload messagePayload, Command command) {
        return Action.builder()
                .client(client)
                .messagePayload(messagePayload)
                .command(command)
                .build();
    }
}
