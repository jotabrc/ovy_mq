package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcher;
import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcherCommand;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.HealthStatus;
import io.github.jotabrc.ovy_mq_core.domain.MessagePayload;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.*;
import static java.util.Objects.nonNull;

@AllArgsConstructor
@Controller
public class MessageController {

    private final PayloadDispatcher payloadDispatcher;
    private final AbstractFactoryResolver factoryResolver;
    private final ObjectProvider<DefinitionMap> definitionProvider;

    @MessageMapping(WS_SAVE)
    public void saveMessage(@Payload MessagePayload messagePayload) {
        if (nonNull(messagePayload)) {
            payloadDispatcher.execute(messagePayload, PayloadDispatcherCommand.SAVE);
        }
    }

    @MessageMapping(WS_MESSAGE)
    public void requestMessage(@Payload String topic, Principal principal) {
        if (nonNull(topic) && !topic.isBlank() && nonNull(principal)) {
            DefinitionMap definition = definitionProvider.getObject()
                    .add(Key.HEADER_CLIENT_ID, principal.getName())
                    .add(Key.HEADER_TOPIC, topic);
            factoryResolver.create(definition, Client.class)
                            .ifPresent(client ->
                                    payloadDispatcher.execute(client, PayloadDispatcherCommand.REQUEST));
        }
    }

    @MessageMapping(WS_MESSAGE + WS_CONFIRM)
    public void confirmProcessing(@Payload MessagePayload messagePayload, @Header(Key.HEADER_TOPIC) String topic) {
        if (nonNull(messagePayload) && nonNull(topic)) {
            messagePayload.setTopic(topic);
            payloadDispatcher.execute(messagePayload, PayloadDispatcherCommand.REMOVE);
        }
    }

    @MessageMapping(WS_HEALTH)
    public void healthCheck(@Payload HealthStatus healthStatus, Principal principal) {
        if (nonNull(healthStatus) && nonNull(principal)) {
            healthStatus.setClientId(principal.getName());
            payloadDispatcher.execute(healthStatus, PayloadDispatcherCommand.HEALTH_CHECK);
        }
    }
}
