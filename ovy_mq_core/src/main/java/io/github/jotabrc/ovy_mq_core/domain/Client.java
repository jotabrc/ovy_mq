package io.github.jotabrc.ovy_mq_core.domain;

import io.github.jotabrc.ovy_mq_core.util.TopicUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.Objects;

@Setter
@Getter
@Builder
public class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String topic;
    @Builder.Default
    private Boolean isAvailable = true;
    private Boolean inboundMessageRequest;
    @Builder.Default
    private Boolean isDestroying = false;
    private String beanName;
    private Method method;
    private ClientType type;
    private ClientConfig config;
    @Builder.Default
    private OffsetDateTime lastHealthCheck = OffsetDateTime.now();
    @Builder.Default
    private OffsetDateTime lastExecution = OffsetDateTime.now();

    public String getTopicForAwaitingProcessingQueue() {
        return TopicUtil.createTopicKey(this.topic, MessageStatus.AWAITING_PROCESSING);
    }

    public void setLastHealthCheck(OffsetDateTime lastHealthCheck) {
        synchronized (this) {
            this.lastHealthCheck = lastHealthCheck;
        }
    }

    public void setAvailable(Boolean available) {
        synchronized (this) {
            isAvailable = available;
        }
    }

    public boolean canDisconnect() {
        return !this.isAvailable && !this.inboundMessageRequest;
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
