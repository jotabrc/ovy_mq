package io.github.jotabrc.ovy_mq.domain;

import io.github.jotabrc.ovy_mq.util.TopicUtil;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("MessagePayload")
public class MessagePayload implements Serializable {

    @Id
    private String id;
    private Object payload;
    private String topic;
    private MessageStatus messageStatus;
    private OffsetDateTime createdDate;
    private boolean success;
    private String clientId;

    public void updateMessageMetadata(String id, OffsetDateTime createdDate) {
        this.id = id;
        this.createdDate = createdDate;
    }

    public void updateMessageStatusTo(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String createTopicKey() {
        return TopicUtil.createTopicKey(this.topic, this.messageStatus);
    }
}
