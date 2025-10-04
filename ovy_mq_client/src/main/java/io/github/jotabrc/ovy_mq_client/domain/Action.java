package io.github.jotabrc.ovy_mq_client.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Action {

    private Client client;
    private MessagePayload messagePayload;
    private Command command;
}
