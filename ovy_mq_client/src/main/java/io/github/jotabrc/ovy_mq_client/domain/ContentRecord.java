package io.github.jotabrc.ovy_mq_client.domain;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private MessagePayload messagePayload;
    private Client client;
}
