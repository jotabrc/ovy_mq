package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcher;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.payload.HealthStatus;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Optional;

import static io.github.jotabrc.ovy_mq_core.constants.Mapping.*;
import static java.util.Objects.nonNull;

@AllArgsConstructor
@Controller
public class MessageController {

    private final PayloadDispatcher payloadDispatcher;
    private final AbstractFactoryResolver factoryResolver;
    private final ObjectProvider<DefinitionMap> definitionProvider;

    @MessageMapping(WS_CLIENT)
    public void process(@Payload OvyAction ovyAction) {
        Optional.ofNullable(ovyAction)
                .ifPresent(action -> action.getCommands()
                        .forEach(wsCommand -> payloadDispatcher.execute(ovyAction, wsCommand)));
    }

    @MessageMapping(WS_SAVE)
    public void saveMessage(@Payload MessagePayload messagePayload) {
        if (nonNull(messagePayload)) {
            payloadDispatcher.execute(messagePayload, io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand.SAVE);
        }
    }

    @MessageMapping(WS_MESSAGE)
    public void requestMessage(@Payload String topic, Principal principal) {
        if (nonNull(topic) && !topic.isBlank() && nonNull(principal)) {
            DefinitionMap definition = definitionProvider.getObject()
                    .add(OvyMqConstants.CLIENT_ID, principal.getName())
                    .add(OvyMqConstants.SUBSCRIBED_TOPIC, topic)
                    .add(OvyMqConstants.CLIENT_TYPE, ClientType.CONSUMER_MESSAGE_REQUEST_BASIC);
            factoryResolver.create(definition, Client.class)
                            .ifPresent(client ->
                                    payloadDispatcher.execute(client, io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand.REQUEST));
        }
    }

    @MessageMapping(WS_MESSAGE + WS_CONFIRM)
    public void confirmProcessing(@Payload MessagePayload messagePayload, @Header(OvyMqConstants.SUBSCRIBED_TOPIC) String topic) {
        if (nonNull(messagePayload) && nonNull(topic)) {
            messagePayload.setTopic(topic);
            payloadDispatcher.execute(messagePayload, io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand.REMOVE);
        }
    }

    @MessageMapping(WS_HEALTH)
    public void healthCheck(@Payload HealthStatus healthStatus, Principal principal) {
        if (nonNull(healthStatus) && nonNull(principal)) {
            healthStatus.setClientId(principal.getName());
            payloadDispatcher.execute(healthStatus, io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand.HEALTH_CHECK);
        }
    }
}
