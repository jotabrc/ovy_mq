package io.github.jotabrc.ovy_mq.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MessageRecord {

    private Client client;
    private MessagePayload messagePayload;
}
