package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.factory.HeaderFactory;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.defaults.Mapping;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.HealthStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadHealthCheckHandler implements PayloadHandler<HealthStatus> {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void handle(HealthStatus healthStatus) {
        log.info("Sending health status: client={}", healthStatus.getClientId());
        healthStatus.setReceivedAt(OffsetDateTime.now());
        healthStatus.setAlive(true);
        sendHealthCheckResponse(healthStatus);
    }

    private void sendHealthCheckResponse(HealthStatus healthStatus) {
        messagingTemplate.convertAndSendToUser(healthStatus.getClientId(),
                Mapping.WS_HEALTH,
                healthStatus,
                HeaderFactory.of(Value.PAYLOAD_TYPE_HEALTH_STATUS));
    }

    @Override
    public Class<HealthStatus> supports() {
        return HealthStatus.class;
    }

    @Override
    public PayloadDispatcherCommand command() {
        return PayloadDispatcherCommand.HEALTH_CHECK;
    }
}
