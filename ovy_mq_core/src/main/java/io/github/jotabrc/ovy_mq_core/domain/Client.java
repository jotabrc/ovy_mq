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

import static java.util.Objects.nonNull;

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

    @JsonIgnore
    public void updateConfig(ListenerConfig listenerConfig) {
        ListenerState newConfig = listenerConfig.getListenerState();
        if (nonNull(newConfig.getReplicas())) this.listenerState.setReplicas(newConfig.getReplicas());
        if (nonNull(newConfig.getMaxReplicas())) this.listenerState.setMaxReplicas(newConfig.getMaxReplicas());
        if (nonNull(newConfig.getMinReplicas())) this.listenerState.setMinReplicas(newConfig.getMinReplicas());
        if (nonNull(newConfig.getStepReplicas())) this.listenerState.setStepReplicas(newConfig.getStepReplicas());
        if (nonNull(newConfig.getTimeout())) this.listenerState.setTimeout(newConfig.getTimeout());
        if (nonNull(newConfig.getAutoManageReplicas())) this.listenerState.setAutoManageReplicas(newConfig.getAutoManageReplicas());
    }
}
