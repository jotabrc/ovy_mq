package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.config.BrokerMapping;
import io.github.jotabrc.ovy_mq.domain.HealthStatus;
import io.github.jotabrc.ovy_mq.domain.factory.HeaderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Component
public class HealthCheck {

    private final SimpMessagingTemplate messagingTemplate;

    public void confirmServerIsAlive(HealthStatus healthStatus) {
        healthStatus.setReceivedAt(OffsetDateTime.now());
        healthStatus.setIsServerAlive(true);
        messagingTemplate.convertAndSendToUser(healthStatus.getRequestedFromClientId(),
                BrokerMapping.HEALTH_CHECK,
                healthStatus,
                HeaderFactory.of("HealthStatus.class"));
    }
}
