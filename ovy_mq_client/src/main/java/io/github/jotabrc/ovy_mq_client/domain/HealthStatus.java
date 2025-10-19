package io.github.jotabrc.ovy_mq_client.domain;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String requestedFromClientId;
    private Boolean isServerAlive;
    private OffsetDateTime receivedAt;
    private OffsetDateTime requestedAt;
}
