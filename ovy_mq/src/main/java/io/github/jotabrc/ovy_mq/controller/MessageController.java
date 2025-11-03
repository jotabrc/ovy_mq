package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq.domain.factory.ClientFactory;
import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcher;
import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcherCommand;
import lombok.AllArgsConstructor;
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

    @MessageMapping(WS_SAVE)
    public void saveMessage(@Payload MessagePayload messagePayload) {
        if (nonNull(messagePayload)) {
            payloadDispatcher.execute(messagePayload, PayloadDispatcherCommand.SAVE);
        }
    }

    @MessageMapping(WS_MESSAGE)
    public void requestMessage(@Payload String topic, Principal principal) {
        if (nonNull(topic) && !topic.isBlank() && nonNull(principal)) {
            payloadDispatcher.execute(ClientFactory.of(principal.getName(), topic), PayloadDispatcherCommand.REQUEST);
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
