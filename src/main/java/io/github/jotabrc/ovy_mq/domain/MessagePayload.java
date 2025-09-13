package io.github.jotabrc.ovy_mq.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("MessagePayload")
public class MessagePayload implements Serializable {

    @Id
    private Long id;

    private String topic;
    private MessageType messageType;
    private Integer retryQuantity;
    private List<OffsetDateTime> retryHistory;
    private OffsetDateTime createdDate;
    private byte[] payload;

}
