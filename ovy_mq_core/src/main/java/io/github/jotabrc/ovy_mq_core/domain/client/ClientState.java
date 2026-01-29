package io.github.jotabrc.ovy_mq_core.domain.client;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private AtomicReference<OffsetDateTime> lastHealthCheck = new AtomicReference<>(OffsetDateTime.now());
    @Builder.Default
    private AtomicReference<OffsetDateTime> lastExecution = new AtomicReference<>(OffsetDateTime.now());

    @Builder.Default
    private AtomicBoolean available = new AtomicBoolean(true);
    @Builder.Default
    private AtomicBoolean messageInteractionActive = new AtomicBoolean(false);
    @Builder.Default
    private AtomicBoolean destroying = new AtomicBoolean(false);
}
