package io.github.jotabrc.ovy_mq_client.domain;


import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.ClientCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClientHandlerAction {

    private Client client;
    private MessagePayload messagePayload;

    public void execute(ClientCommand clientCommand) {
        clientCommand.execute(this);
    }
}
