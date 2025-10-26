package io.github.jotabrc.ovy_mq_client.domain;

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
@Builder
@Getter
public class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String topic;
    private Boolean isAvailable;
    private Object beanInstance;
    private Method method;
    private ClientSessionHandler clientSessionHandler;
    @Builder.Default
    private OffsetDateTime lastHealthCheckResponse = OffsetDateTime.now();

    public void requestMessage() {
        this.clientSessionHandler.getSession().send(StompHeaderFactory.get(this.topic, WS_REQUEST + WS_MESSAGE), this.topic);
    }

    public void confirmPayloadReceived(MessagePayload messagePayload) {
        this.clientSessionHandler.getSession()
                .send(StompHeaderFactory.get(this.topic, WS_REQUEST + WS_MESSAGE + WS_CONFIRM),
                        messagePayload.cleanDataAndUpdateSuccessTo(true)
                );
    }

    public void requestHealthCheck() {
        HealthStatus healthStatus = HealthStatus.builder()
                .requestedAt(OffsetDateTime.now())
                .isServerAlive(false)
                .build();
        this.clientSessionHandler.getSession().send(StompHeaderFactory.get(this.topic, WS_REQUEST + WS_HEALTH), healthStatus);
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
