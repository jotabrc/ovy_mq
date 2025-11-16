package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.registry.ConfigClientContextHolder;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.components.MapCreator;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Mapping;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import io.github.jotabrc.ovy_mq_core.factories.AbstractFactoryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadListenerConfigHandler implements PayloadHandler<ListenerConfig> {

    private final AbstractFactoryResolver factoryResolver;
    private final ConfigClientContextHolder configClientContextHolder;
    private final SimpMessagingTemplate messagingTemplate;
    private final MapCreator mapCreator;

    @Override
    public void handle(ListenerConfig listenerConfig) {
        log.info("Sending listener config: topic={} replica-config=[quantity={} max={} min={} step={} autoManage={} timeout={}ms]",
                listenerConfig.getListenerState().getTopic(),
                listenerConfig.getListenerState().getReplica().getQuantity(),
                listenerConfig.getListenerState().getReplica().getMax(),
                listenerConfig.getListenerState().getReplica().getMin(),
                listenerConfig.getListenerState().getReplica().getMin(),
                listenerConfig.getListenerState().getReplica().getAutoManage(),
                listenerConfig.getListenerState().getTimeout());
        sendConfig(listenerConfig);
    }

    private void sendConfig(ListenerConfig listenerConfig) {
        configClientContextHolder.getId()
                .ifPresentOrElse(clientId -> {
                            var definitions = mapCreator.create(mapCreator.createDto(Key.HEADER_CLIENT_ID, clientId),
                                    mapCreator.createDto(Key.HEADER_PAYLOAD_TYPE, Value.PAYLOAD_TYPE_LISTENER_CONFIG));
                            factoryResolver.create(definitions, MessageHeaders.class)
                                    .ifPresent(headers -> messagingTemplate.convertAndSendToUser(clientId,
                                            Mapping.WS_CONFIG,
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
