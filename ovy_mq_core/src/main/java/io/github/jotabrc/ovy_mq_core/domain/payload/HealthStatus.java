package io.github.jotabrc.ovy_mq_core.domain.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Builder.Default
    private Boolean alive = false;
    private OffsetDateTime receivedAt;
    private OffsetDateTime requestedAt;

    @JsonIgnore
    public Long responseTime() {
        return nonNull(receivedAt) && nonNull(requestedAt)
                ? receivedAt.toInstant().toEpochMilli() - requestedAt.toInstant().toEpochMilli()
                : null;
    }
}
