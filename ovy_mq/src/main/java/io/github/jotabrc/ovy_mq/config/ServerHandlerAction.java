package io.github.jotabrc.ovy_mq.config;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.service.handler.ServerCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ServerHandlerAction {

    private Client client;
    private MessagePayload messagePayload;

    public void execute(ServerCommand serverCommand) {
        serverCommand.execute(this);
    }
}
