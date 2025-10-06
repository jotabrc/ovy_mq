package io.github.jotabrc.ovy_mq_client.domain.factory;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.ClientHandlerAction;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;

public class HandlerActionFactory {

    private HandlerActionFactory() {}

    public static ClientHandlerAction of(Client client, MessagePayload messagePayload) {
        return ClientHandlerAction.builder()
                .client(client)
                .messagePayload(messagePayload)
                .build();
    }

    public static ClientHandlerAction of(Client client) {
        return ClientHandlerAction.builder()
                .client(client)
                .build();
    }

    public static ClientHandlerAction and() {
        return ClientHandlerAction.builder()
                .build();
    }
}
