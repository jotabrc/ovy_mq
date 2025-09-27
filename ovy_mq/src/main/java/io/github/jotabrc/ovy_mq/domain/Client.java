package io.github.jotabrc.ovy_mq.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Objects;

@Builder
@Getter
public class Client {

    private String id;
    private String listeningTopic;
    private Boolean isAvailable;
    private OffsetDateTime lastUsed;

    public void updateStatus() {
        this.lastUsed = OffsetDateTime.now();
        this.isAvailable = !this.isAvailable;
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
