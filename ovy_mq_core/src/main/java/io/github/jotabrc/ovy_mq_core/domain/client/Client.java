package io.github.jotabrc.ovy_mq_core.domain.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessageStatus;
import io.github.jotabrc.ovy_mq_core.util.TopicUtil;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String topic;
    private ClientType type;
    private ListenerConfig config;
    private ClientState state;
    @JsonIgnore
    private transient ClientExecution execution;

    @JsonIgnore
    public String getTopicForAwaitingProcessingQueue() {
        return TopicUtil.createTopicKey(this.topic, MessageStatus.AWAITING_PROCESSING);
    }

    @JsonIgnore
    public void setLastHealthCheck(OffsetDateTime lastHealthCheck) {
        this.state.getLastHealthCheck().set(lastHealthCheck);
    }

    @JsonIgnore
    public void setLastExecution(OffsetDateTime lastExecution) {
        this.state.getLastExecution().set(lastExecution);
    }

    @JsonIgnore
    public void setAvailable(Boolean available) {
        this.state.getAvailable().set(available);
    }

    @JsonIgnore
    public void setMessageInteractionActive(Boolean messageInteractionActive) {
        this.state.getMessageInteractionActive().set(messageInteractionActive);
    }

    @JsonIgnore
    public void setDestroying(Boolean destroying) {
        this.state.getDestroying().set(destroying);
    }

    @JsonIgnore
    public boolean canDisconnect() {
        return this.state.getAvailable().get() && !this.state.getMessageInteractionActive().get();
    }

    @JsonIgnore
    public Boolean useGlobalValues() {
        return nonNull(this.config)
                && nonNull(this.config.getUseGlobalValues())
                && this.config.getUseGlobalValues();
    }

    @JsonIgnore
    public Long getHealthCheckInitialDelay() {
        return nonNull(this.config) && nonNull(this.config.getHealthCheckInitialDelay())
                ? this.config.getHealthCheckInitialDelay()
                : null;
    }

    @JsonIgnore
    public Long getHealthCheckFixedDelay() {
        return nonNull(this.config) && nonNull(this.config.getHealthCheckFixedDelay())
                ? this.config.getHealthCheckFixedDelay()
                : null;
    }

    @JsonIgnore
    public Long getHealthCheckExpirationTime() {
        return nonNull(this.config) && nonNull(this.config.getHealthCheckExpirationTime())
                ? this.config.getHealthCheckExpirationTime()
                : null;
    }

    @JsonIgnore
    public Long getPollInitialDelay() {
        return nonNull(this.config) && nonNull(this.config.getPollInitialDelay())
                ? this.config.getPollInitialDelay()
                : null;
    }

    @JsonIgnore
    public Long getPollFixedDelay() {
        return nonNull(this.config) && nonNull(this.config.getPollFixedDelay())
                ? this.config.getPollFixedDelay()
                : null;
    }

    @JsonIgnore
    public Long getConnectionTimeout() {
        return nonNull(this.config) && nonNull(this.config.getConnectionTimeout())
                ? this.config.getConnectionTimeout()
                : null;
    }

    @JsonIgnore
    public Integer getConnectionMaxRetries() {
        return nonNull(this.config) && nonNull(this.config.getConnectionMaxRetries())
                ? this.config.getConnectionMaxRetries()
                : null;
    }

    @JsonIgnore
    public Long getProcessingTimeout() {
        return nonNull(this.config) && nonNull(this.config.getProcessingTimeout())
                ? this.config.getProcessingTimeout()
                : null;
    }

    @JsonIgnore
    public Client getBasicClient() {
        return Client.builder()
                .id(this.id)
                .topic(this.topic)
                .type(ClientType.CONSUMER_MESSAGE_REQUEST_BASIC)
                .build();
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
