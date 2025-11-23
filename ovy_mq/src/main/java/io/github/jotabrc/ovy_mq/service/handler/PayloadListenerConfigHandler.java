package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.registry.ConfigClientContextHolder;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Mapping;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
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
    private final ObjectProvider<DefinitionMap> definitionProvider;

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
                    DefinitionMap definition = definitionProvider.getObject()
                            .add(Key.HEADER_CLIENT_ID, clientId)
                            .add(Key.HEADER_CLIENT_TYPE, ClientType.CONFIGURER.name())
                            .add(Key.HEADER_PAYLOAD_TYPE, Value.PAYLOAD_TYPE_LISTENER_CONFIG);
                            factoryResolver.create(definition, MessageHeaders.class)
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
