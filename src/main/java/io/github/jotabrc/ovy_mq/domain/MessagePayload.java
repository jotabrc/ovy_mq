package io.github.jotabrc.ovy_mq.domain;

import io.github.jotabrc.ovy_mq.util.TopicUtil;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("MessagePayload")
public class MessagePayload implements Serializable {

    @Id
    private String id;
    private byte[] payload;
    private String topic;
    private MessageStatus messageStatus;
    private OffsetDateTime createdDate;
    private boolean success;

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

    public String toJSON() {
        return new StringJoiner(", ", "{", "}")
                .add("\"id\":\"" + id + "\"")
                .add("\"payload\":" + Arrays.toString(payload))
                .add("\"topic\":\"" + topic + "\"")
                .add("\"messageStatus\":" + messageStatus)
                .add("\"createdDate\":" + createdDate)
                .add("\"success\":" + success)
                .toString();
    }
}
