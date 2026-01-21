package io.github.jotabrc.ovy_mq_core.domain.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.jotabrc.ovy_mq_core.util.TopicUtil;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

import static java.util.Objects.nonNull;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class MessagePayload implements Serializable {

    public MessagePayload() {
        this.messageStatus = MessageStatus.AWAITING_PROCESSING;
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private Object payload;
    private String topic;
    @Builder.Default
    private MessageStatus messageStatus = MessageStatus.AWAITING_PROCESSING;
    private OffsetDateTime createdDate;
    @JsonIgnore
    private OffsetDateTime processingStartedAt;
    private boolean success;
    private Long version;

    @JsonIgnore
    public MessagePayload cleanDataAndUpdateSuccessTo(boolean success) {
        return MessagePayload.builder()
                .id(this.id)
                .topic(this.topic)
                .success(success)
                .build();
    }

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
    public String getTopicKey() {
        return (success)
                ? TopicUtil.createTopicKeyForSent(this.topic)
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