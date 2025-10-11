package io.github.jotabrc.ovy_mq.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.jotabrc.ovy_mq.util.TopicUtil;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

import static java.util.Objects.nonNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessagePayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private Object payload;
    private String listeningTopic;
    private MessageStatus messageStatus;
    private OffsetDateTime createdDate;
    private boolean success;

    @JsonIgnore
    public void updateMessageMetadata(String id, OffsetDateTime createdDate) {
        this.id = id;
        this.createdDate = createdDate;
    }

    @JsonIgnore
    public void updateMessageStatusTo(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    @JsonIgnore
    public String getListeningTopic() {
        return (success)
                ? TopicUtil.createTopicKeyForProcessing(this.listeningTopic)
                : TopicUtil.createTopicKey(this.listeningTopic, this.messageStatus);
    }

    @JsonIgnore
    public boolean hasTopic() {
        return nonNull(this.listeningTopic);
    }

    @JsonIgnore
    public boolean isProcessable() {
        return nonNull(this.payload) && nonNull(this.listeningTopic);
    }
}
