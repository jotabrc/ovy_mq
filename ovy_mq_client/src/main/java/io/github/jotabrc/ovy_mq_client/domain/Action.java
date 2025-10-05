package io.github.jotabrc.ovy_mq_client.domain;


import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.AbstractHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Action {

    private Client client;
    private MessagePayload messagePayload;
    private Command command;
    private AbstractHandler abstractHandler;

    public void execute(Command command) {
        command.execute(this);
    }
}
