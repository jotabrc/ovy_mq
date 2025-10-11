package io.github.jotabrc.ovy_mq_client.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessagePayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private Object payload;
    private String topic;
    private MessageStatus messageStatus;
    private OffsetDateTime createdDate;
    private boolean success;

    @JsonIgnore
    public void cleanDataAndUpdateSuccessValue(boolean success) {
        this.payload = null;
        this.success = success;
    }
}