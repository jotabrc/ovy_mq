package io.github.jotabrc.ovy_mq.domain;

import io.github.jotabrc.ovy_mq.domain.defaults.MessageStatus;
import io.github.jotabrc.ovy_mq.util.TopicUtil;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

@Builder
@Getter
public class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String topic;
    private Boolean isAvailable;
    private Method method;

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
