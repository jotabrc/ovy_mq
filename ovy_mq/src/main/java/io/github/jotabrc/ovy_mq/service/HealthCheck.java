package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.config.Mapping;
import io.github.jotabrc.ovy_mq.domain.HealthStatus;
import io.github.jotabrc.ovy_mq.domain.factory.HeaderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class HealthCheck {

    private final SimpMessagingTemplate messagingTemplate;

    public void confirmServerIsAlive(HealthStatus healthStatus) {
        log.info("Sending health status to client={}", healthStatus.getRequestedFromClientId());
        healthStatus.setReceivedAt(OffsetDateTime.now());
        healthStatus.setIsServerAlive(true);
        messagingTemplate.convertAndSendToUser(healthStatus.getRequestedFromClientId(),
                Mapping.WS_HEALTH,
                healthStatus,
                HeaderFactory.of("health-status"));
    }
}
