package io.github.jotabrc.ovy_mq_core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Builder
public class ConfigClient implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private ClientType type;

    @Override
    public boolean equals(Object o) {
        if (Objects.isNull(o) || !Objects.equals(getClass(), o.getClass())) return false;

        ConfigClient client = (ConfigClient) o;
        return id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id) * 31;
    }
}
