package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.registry.ClientConfigurerContextHolder;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.client.ListenerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static io.github.jotabrc.ovy_mq_core.constants.Mapping.WS_CONFIG;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadListenerConfigHandler implements PayloadHandler {

    private final AbstractFactoryResolver factoryResolver;
    private final ClientConfigurerContextHolder clientConfigurerContextHolder;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectProvider<DefinitionMap> definitionProvider;

    @Override
    public void handle(OvyAction ovyAction) {
        // todo: log new config
        ListenerConfig listenerConfig = ovyAction.getDefinitionMap().extract(OvyMqConstants.OBJECT_LISTENER_CONFIG, ListenerConfig.class);
        sendConfig(listenerConfig);
    }

    private void sendConfig(ListenerConfig listenerConfig) {
        clientConfigurerContextHolder.getId()
                .ifPresentOrElse(clientId -> {
                    DefinitionMap definition = definitionProvider.getObject()
                            .add(OvyMqConstants.CLIENT_ID, clientId)
                            .add(OvyMqConstants.CLIENT_TYPE, ClientType.CONFIGURER.name())
                            .add(OvyMqConstants.PAYLOAD_TYPE, OvyMqConstants.PAYLOAD_TYPE_LISTENER_CONFIG);
                            factoryResolver.create(definition, MessageHeaders.class)
                                    .ifPresent(headers -> messagingTemplate.convertAndSendToUser(clientId,
                                            WS_CONFIG,
                                            listenerConfig,
                                            headers));
                        },
                        () -> log.info("ClientConfigurer not found, unable to send configuration to client."));
    }

    @Override
    public io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand command() {
        return io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand.LISTENER_CONFIG;
    }
}
