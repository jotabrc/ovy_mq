package io.github.jotabrc.ovy_mq_client.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.jotabrc.ovy_mq_client.domain.factory.StompHeaderFactory;
import io.github.jotabrc.ovy_mq_client.service.handler.ClientSessionHandler;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.Objects;

import static io.github.jotabrc.ovy_mq_client.config.Mapping.*;

@Setter
@Getter
@Builder
public class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String topic;
    private Boolean isAvailable;
    private String beanName;
    private Method method;
    private ClientSessionHandler clientSessionHandler;
    @Builder.Default
    private OffsetDateTime lastHealthCheck = OffsetDateTime.now();
    @Builder.Default
    private OffsetDateTime lastExecution = OffsetDateTime.now();
    ListenerState listenerState;

    @JsonIgnore
    public Runnable requestMessage() {
        return this.isAvailable
                ? () -> this.clientSessionHandler.getSession().send(StompHeaderFactory.get(this.topic, WS_REQUEST + WS_MESSAGE), this.topic)
                : null;
    }

    @JsonIgnore
    public Runnable confirmPayloadReceived(MessagePayload messagePayload) {
        return () -> this.clientSessionHandler.getSession().send(StompHeaderFactory.get(this.topic, WS_REQUEST + WS_MESSAGE + WS_CONFIRM),
                messagePayload.cleanDataAndUpdateSuccessTo(true));
    }

    @JsonIgnore
    public Runnable requestHealthCheck() {
        HealthStatus healthStatus = HealthStatus.builder()
                .requestedAt(OffsetDateTime.now())
                .alive(false)
                .build();
        return () -> this.clientSessionHandler.getSession().send(StompHeaderFactory.get(this.topic, WS_REQUEST + WS_HEALTH), healthStatus);
    }

    @JsonIgnore
    public void disconnect() {
        if (this.getClientSessionHandler().getSession().isConnected()) {
            this.getClientSessionHandler().getSession().disconnect();
        }
    }

    @JsonIgnore
    public boolean isConnected() {
        return this.getClientSessionHandler().getSession().isConnected();
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
