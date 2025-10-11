package io.github.jotabrc.ovy_mq.domain;

import io.github.jotabrc.ovy_mq.util.TopicUtil;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.Objects;

@Builder
@Getter
public class Client {

    private String id;
    private String topic;
    private Boolean isAvailable;
    private OffsetDateTime lastUsed;
    private ClientType type;
    private Long replicas;
    private Long replicasInUse;
    private Method method;

    public void updateStatus() {
        this.lastUsed = OffsetDateTime.now();
        this.isAvailable = !this.isAvailable;
    }

    public String getTopicForAwaitingProcessingQueue() {
        return TopicUtil.createTopicKey(this.topic, MessageStatus.AWAITING_PROCESSING);
    }

    @Override
    public boolean equals(Object o) {
        if (Objects.isNull(o) || !Objects.equals(getClass(), o.getClass())) return false;

        Client client = (Client) o;
        return id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id) * 31;
    }
}
