package io.github.jotabrc.ovy_mq_core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.jotabrc.ovy_mq_core.util.TopicUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.Objects;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.*;

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
    @Builder.Default
    private OffsetDateTime lastHealthCheck = OffsetDateTime.now();
    @Builder.Default
    private OffsetDateTime lastExecution = OffsetDateTime.now();
    private ListenerState listenerState;

    public String getTopicForAwaitingProcessingQueue() {
        return TopicUtil.createTopicKey(this.topic, MessageStatus.AWAITING_PROCESSING);
    }

    @JsonIgnore
    public String requestMessage() {
        return this.isAvailable
                ? WS_REQUEST + WS_MESSAGE
                : null;
    }

    @JsonIgnore
    public String confirmPayloadReceived() {
        return WS_REQUEST + WS_MESSAGE + WS_CONFIRM;
    }

    @JsonIgnore
    public String requestHealthCheck() {
        return WS_REQUEST + WS_HEALTH;
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
