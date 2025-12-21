package io.github.jotabrc.ovy_mq_core.domain.client;

import io.github.jotabrc.ovy_mq_core.domain.payload.MessageStatus;
import io.github.jotabrc.ovy_mq_core.util.TopicUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Setter
@Getter
@Builder
public class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String topic;

    private String beanName;
    private Method method;

    private ClientType type;
    private ListenerConfig config;

    @Builder.Default
    private OffsetDateTime lastHealthCheck = OffsetDateTime.now();
    @Builder.Default
    private OffsetDateTime lastExecution = OffsetDateTime.now();

    @Builder.Default
    private Boolean isAvailable = true;
    @Builder.Default
    private Boolean isMessageInteractionActive = false;
    @Builder.Default
    private Boolean isDestroying = false;

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
        return this.isAvailable && !this.isMessageInteractionActive;
    }

    public Boolean useGlobalValues() {
        return nonNull(this.config)
                && nonNull(this.config.getUseGlobalValues())
                && this.config.getUseGlobalValues();
    }

    public Long getHealthCheckInitialDelay() {
        return nonNull(this.config) && nonNull(this.config.getHealthCheckInitialDelay())
                ? this.config.getHealthCheckInitialDelay()
                : null;
    }

    public Long getHealthCheckFixedDelay() {
        return nonNull(this.config) && nonNull(this.config.getHealthCheckFixedDelay())
                ? this.config.getHealthCheckFixedDelay()
                : null;
    }

    public Long getHealthCheckExpirationTime() {
        return nonNull(this.config) && nonNull(this.config.getHealthCheckExpirationTime())
                ? this.config.getHealthCheckExpirationTime()
                : null;
    }

    public Long getPollInitialDelay() {
        return nonNull(this.config) && nonNull(this.config.getPollInitialDelay())
                ? this.config.getPollInitialDelay()
                : null;
    }

    public Long getPollFixedDelay() {
        return nonNull(this.config) && nonNull(this.config.getPollFixedDelay())
                ? this.config.getPollFixedDelay()
                : null;
    }

    public Long getConnectionTimeout() {
        return nonNull(this.config) && nonNull(this.config.getConnectionTimeout())
                ? this.config.getConnectionTimeout()
                : null;
    }

    public Integer getConnectionMaxRetries() {
        return nonNull(this.config) && nonNull(this.config.getConnectionMaxRetries())
                ? this.config.getConnectionMaxRetries()
                : null;
    }

    public Long getProcessingTimeout() {
        return nonNull(this.config) && nonNull(this.config.getProcessingTimeout())
                ? this.config.getProcessingTimeout()
                : null;
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
