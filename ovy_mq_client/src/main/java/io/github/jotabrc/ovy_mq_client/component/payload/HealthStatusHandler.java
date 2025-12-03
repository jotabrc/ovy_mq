package io.github.jotabrc.ovy_mq_client.component.payload;

import io.github.jotabrc.ovy_mq_client.component.payload.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.payload.HealthStatus;
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

    @Async
    @Override
    public void handle(Client client, HealthStatus payload, StompHeaders headers) {
        handle(client, payload);
    }

    private void handle(Client client, HealthStatus healthStatus) {
        log.info("Health check completed: client={} received response-time={}ms", client.getId(), healthStatus.responseTime());
        client.setLastHealthCheck(OffsetDateTime.now());
    }

    @Override
    public Class<HealthStatus> supports() {
        return HealthStatus.class;
    }
}
