package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.factory.domain.MessageHeadersDto;
import io.github.jotabrc.ovy_mq.registry.ConfigClientContextHolder;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.defaults.Mapping;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import io.github.jotabrc.ovy_mq_core.factories.AbstractFactoryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadListenerConfigHandler implements PayloadHandler<ListenerConfig> {

    private final AbstractFactoryResolver factoryResolver;
    private final ConfigClientContextHolder configClientContextHolder;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void handle(ListenerConfig listenerConfig) {
        log.info("Sending listener config: topic={} config=[replicas={} maxReplicas={} minReplicas={} stepReplicas={} timeout={} autoManageReplicas={}]",
                listenerConfig.getListenerState().getTopic(),
                listenerConfig.getListenerState().getReplicas(),
                listenerConfig.getListenerState().getMaxReplicas(),
                listenerConfig.getListenerState().getMinReplicas(),
                listenerConfig.getListenerState().getStepReplicas(),
                listenerConfig.getListenerState().getTimeout(),
                listenerConfig.getListenerState().getAutoManageReplicas());
        sendConfig(listenerConfig);
    }

    private void sendConfig(ListenerConfig listenerConfig) {
        configClientContextHolder.getId()
                .ifPresentOrElse(clientId -> {

                            MessageHeadersDto dto = new MessageHeadersDto(clientId,
                                    Value.PAYLOAD_TYPE_LISTENER_CONFIG);
                            factoryResolver.create(dto, dto.getReturns())
                                    .ifPresent(headers -> messagingTemplate.convertAndSendToUser(clientId,
                                            Mapping.WS_CONFIG + "/" + listenerConfig.getListenerState().getTopic(),
                                            listenerConfig,
                                            headers));
                        },
                        () -> log.info("ConfigClient not found, unable to send configuration to client."));
    }

    @Override
    public Class<ListenerConfig> supports() {
        return ListenerConfig.class;
    }

    @Override
    public PayloadDispatcherCommand command() {
        return PayloadDispatcherCommand.LISTENER_CONFIG;
    }
}
