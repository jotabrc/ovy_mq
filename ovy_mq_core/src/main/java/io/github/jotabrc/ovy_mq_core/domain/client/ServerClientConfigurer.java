package io.github.jotabrc.ovy_mq_core.domain.client;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Builder
public class ServerClientConfigurer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private ClientType type;

    @Override
    public boolean equals(Object o) {
        if (Objects.isNull(o) || !Objects.equals(getClass(), o.getClass())) return false;

        ServerClientConfigurer client = (ServerClientConfigurer) o;
        return id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id) * 31;
    }
}
