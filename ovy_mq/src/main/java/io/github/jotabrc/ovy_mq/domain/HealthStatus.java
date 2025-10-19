package io.github.jotabrc.ovy_mq.domain;

import lombok.*;

import java.time.OffsetDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatus {

    private String requestedFromClientId;
    private Boolean isServerAlive;
    private OffsetDateTime receivedAt;
    private OffsetDateTime requestedAt;
}
