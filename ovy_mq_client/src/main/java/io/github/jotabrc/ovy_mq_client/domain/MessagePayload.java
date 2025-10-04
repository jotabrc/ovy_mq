package io.github.jotabrc.ovy_mq_client.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessagePayload implements Serializable {

    private String id;
    private Object payload;
    private String topic;
    private MessageStatus messageStatus;
    private OffsetDateTime createdDate;
    private boolean success;

}