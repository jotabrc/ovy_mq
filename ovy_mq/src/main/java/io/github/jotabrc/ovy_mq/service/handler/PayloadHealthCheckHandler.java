package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.components.MapCreator;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Mapping;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.HealthStatus;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadHealthCheckHandler implements PayloadHandler<HealthStatus> {

    private final AbstractFactoryResolver factoryResolver;
    private final SimpMessagingTemplate messagingTemplate;
    private final MapCreator mapCreator;

    @Override
    public void handle(HealthStatus healthStatus) {
        log.info("Sending health status: client={}", healthStatus.getClientId());
        healthStatus.setReceivedAt(OffsetDateTime.now());
        healthStatus.setAlive(true);
        sendHealthCheckResponse(healthStatus);
    }

    private void sendHealthCheckResponse(HealthStatus healthStatus) {
        var definitions = mapCreator.create(mapCreator.createDto(Key.HEADER_CLIENT_ID, healthStatus.getClientId()),
                mapCreator.createDto(Key.HEADER_PAYLOAD_TYPE, Value.PAYLOAD_TYPE_HEALTH_STATUS),
                mapCreator.createDto(Key.HEADER_CLIENT_TYPE, ClientType.SERVER.name()));
        factoryResolver.create(definitions, MessageHeaders.class)
                .ifPresent(headers -> messagingTemplate.convertAndSendToUser(healthStatus.getClientId(),
                        Mapping.WS_HEALTH,
                        healthStatus,
                        headers));

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
