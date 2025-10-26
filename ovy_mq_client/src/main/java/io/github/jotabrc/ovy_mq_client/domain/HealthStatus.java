package io.github.jotabrc.ovy_mq_client.domain;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

import static java.util.Objects.nonNull;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String clientId;
    private Boolean isServerAlive;
    private OffsetDateTime receivedAt;
    private OffsetDateTime requestedAt;

    public Long responseTime() {
        return nonNull(receivedAt) && nonNull(requestedAt)
                ? receivedAt.toInstant().toEpochMilli() - requestedAt.toInstant().toEpochMilli()
                : null;
    }
}
