package io.github.jotabrc.ovy_mq_client.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Builder
public class MessagePayload implements Serializable {

    private String id;
    private byte[] payload;
    private String topic;
    private MessageStatus messageStatus;
    private OffsetDateTime createdDate;

    @Setter
    private boolean success;
}
