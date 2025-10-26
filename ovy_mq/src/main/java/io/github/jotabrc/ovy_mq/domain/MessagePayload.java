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
    private String topic;
    private MessageStatus messageStatus;
    private OffsetDateTime createdDate;
    @JsonIgnore
    private OffsetDateTime processingStartedAt;
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
    public String getTopic() {
        return (success)
                ? TopicUtil.createTopicKeyForProcessing(this.topic)
                : TopicUtil.createTopicKey(this.topic, this.messageStatus);
    }

    @JsonIgnore
    public boolean hasTopic() {
        return nonNull(this.topic);
    }

    @JsonIgnore
    public boolean isProcessable() {
        return nonNull(this.payload) && nonNull(this.topic);
    }

    @JsonIgnore
    public boolean hasIdentifiers() {
        return nonNull(topic) && !topic.isBlank() && nonNull(id) && !id.isBlank();
    }

    @JsonIgnore
    public long getMsSinceStartedProcessing() {
        return this.processingStartedAt.toEpochSecond() - OffsetDateTime.now().toEpochSecond();
    }
}
