package io.github.jotabrc.ovy_mq_core.domain.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.jotabrc.ovy_mq_core.components.mapper.PayloadDeserializer;
import io.github.jotabrc.ovy_mq_core.components.mapper.PayloadSerializer;
import io.github.jotabrc.ovy_mq_core.util.TopicUtil;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

import static java.util.Objects.nonNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessagePayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;

    @JsonSerialize(using = PayloadSerializer.class)
    @JsonDeserialize(using = PayloadDeserializer.class)
    private Object payload;
    private String payloadType;

    private String topic;
    private MessageStatus messageStatus = MessageStatus.AWAITING_PROCESSING;
    private OffsetDateTime createdDate;
    @JsonIgnore
    private OffsetDateTime processingStartedAt;
    private boolean success;
    private Long version;

    @JsonIgnore
    public MessagePayload cleanDataAndReturnWithStatus(MessageStatus messageStatus) {
            return MessagePayload.builder()
                    .id(this.id)
                    .topic(this.topic)
                    .messageStatus(messageStatus)
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
        return TopicUtil.createTopicKey(this.topic, this.messageStatus);
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

    public void setPayload(Object payload) {
        this.payload = payload;
        if (nonNull(payload)) {
            this.payloadType = payload.getClass().getName();
        }
    }

    private String payloadTypeFrom() {
        return payload.getClass().getName();
    }


    private MessagePayload(Builder builder) {
        this.id = builder.id;
        this.payload = builder.payload;
        this.topic = builder.topic;
        this.messageStatus = builder.messageStatus;
        this.createdDate = builder.createdDate;
        this.processingStartedAt = builder.processingStartedAt;
        this.version = builder.version;
        if (nonNull(builder.payload)) {
            this.payloadType = builder.payload.getClass().getName();
        }
    }

    public static class Builder {
        private String id;
        private Object payload;
        private String topic;
        private MessageStatus messageStatus = MessageStatus.AWAITING_PROCESSING;
        private OffsetDateTime createdDate;
        private OffsetDateTime processingStartedAt;
        private Long version;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder messageStatus(MessageStatus messageStatus) {
            this.messageStatus = messageStatus;
            return this;
        }

        public Builder createdDate(OffsetDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder processingStartedAt(OffsetDateTime processingStartedAt) {
            this.processingStartedAt = processingStartedAt;
            return this;
        }

        public Builder version(Long version) {
            this.version = version;
            return this;
        }

        public MessagePayload build() {
            return new MessagePayload(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}