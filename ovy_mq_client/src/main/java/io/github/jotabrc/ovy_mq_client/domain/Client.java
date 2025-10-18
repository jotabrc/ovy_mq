package io.github.jotabrc.ovy_mq_client.domain;

import io.github.jotabrc.ovy_mq_client.domain.factory.StompHeaderFactory;
import io.github.jotabrc.ovy_mq_client.service.ClientSession;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

@Setter
@Builder
@Getter
public class Client implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String topic;
    private Boolean isAvailable;
    private Method method;
    private ClientSession clientSession;

    public void requestMessage() {
        this.clientSession.getSession().send(StompHeaderFactory.get(this.topic, "/request/message"), this.topic);
    }

    public void confirmProcessing(MessagePayload messagePayload) {
        this.clientSession.getSession().send(StompHeaderFactory.get(this.topic, "/request/message/confirm"), messagePayload);
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
