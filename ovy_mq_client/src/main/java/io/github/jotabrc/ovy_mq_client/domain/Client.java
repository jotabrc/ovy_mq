package io.github.jotabrc.ovy_mq_client.domain;

import io.github.jotabrc.ovy_mq_client.service.domain.client.ClientSession;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.Objects;

@Setter
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
    private ClientSession clientSession;

    public void requestMessage() {
        this.clientSession.getSession().send("/request/message", new MessagePayload());
    }
    public void confirmProcessing(MessagePayload messagePayload) {
        this.clientSession.getSession().send("/request/message/confirm", messagePayload);
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
