package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.Mapping;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.payload.HealthStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
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
    private final ObjectProvider<DefinitionMap> definitionProvier;

    @Override
    public void handle(HealthStatus healthStatus) {
        log.info("Sending health status: client={}", healthStatus.getClientId());
        healthStatus.setReceivedAt(OffsetDateTime.now());
        healthStatus.setAlive(true);
        sendHealthCheckResponse(healthStatus);
    }

    private void sendHealthCheckResponse(HealthStatus healthStatus) {
        DefinitionMap definition = definitionProvier.getObject()
                .add(OvyMqConstants.CLIENT_ID, healthStatus.getClientId())
                .add(OvyMqConstants.PAYLOAD_TYPE, OvyMqConstants.PAYLOAD_TYPE_HEALTH_STATUS)
                .add(OvyMqConstants.CLIENT_TYPE, ClientType.SERVER.name());
        factoryResolver.create(definition, MessageHeaders.class)
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
