package io.github.jotabrc.ovy_mq_client.service.handler;

import io.github.jotabrc.ovy_mq_client.domain.HealthStatus;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientHealthCheckHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientHealthCheckHandlerImpl implements ClientHealthCheckHandler {

    private final ClientRegistry clientRegistry;

    @Override
    public void handle(String clientId, HealthStatus healthStatus) {
        log.info("Health check response for client={} received", clientId);
        clientRegistry.getByClientIdOrThrow(clientId)
                .setLastHealthCheckResponse(OffsetDateTime.now());
    }
}
