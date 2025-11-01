package io.github.jotabrc.ovy_mq_client.service.handler.payload;

import io.github.jotabrc.ovy_mq_client.domain.HealthStatus;
import io.github.jotabrc.ovy_mq_client.service.handler.payload.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class HealthStatusHandler implements PayloadHandler<HealthStatus> {

    private final ClientRegistry clientRegistry;

    @Async
    @Override
    public void handle(String clientId, HealthStatus payload, StompHeaders headers) {
        handle(clientId, payload);
    }

    private void handle(String clientId, HealthStatus healthStatus) {
        log.info("Health check response for client={} received, response-time={} ms", clientId, healthStatus.responseTime());
        clientRegistry.getByClientIdOrThrow(clientId).setLastHealthCheckResponse(OffsetDateTime.now());
    }

    @Override
    public Class<HealthStatus> supports() {
        return HealthStatus.class;
    }
}
