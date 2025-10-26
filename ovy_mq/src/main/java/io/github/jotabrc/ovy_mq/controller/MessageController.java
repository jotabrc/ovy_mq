package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.domain.HealthStatus;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.factory.ClientFactory;
import io.github.jotabrc.ovy_mq.service.handler.PayloadExecutor;
import io.github.jotabrc.ovy_mq.service.handler.PayloadHandlerCommand;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static io.github.jotabrc.ovy_mq.config.Mapping.*;
import static java.util.Objects.nonNull;

@AllArgsConstructor
@Controller
public class MessageController {

    private final PayloadExecutor payloadExecutor;

    @MessageMapping(WS_SAVE)
    public void saveMessage(@Payload MessagePayload messagePayload) {
        if (nonNull(messagePayload)) {
            payloadExecutor.execute(messagePayload, PayloadHandlerCommand.SAVE);
        }
    }

    @MessageMapping(WS_MESSAGE)
    public void requestMessage(@Payload String topic, Principal principal) {
        if (nonNull(topic) && !topic.isBlank() && nonNull(principal)) {
            payloadExecutor.execute(ClientFactory.of(principal.getName(), topic), PayloadHandlerCommand.REQUEST);
        }
    }

    @MessageMapping(WS_MESSAGE + WS_CONFIRM)
    public void confirmProcessing(@Payload MessagePayload messagePayload, @Header("Listening-Topic") String topic) {
        if (nonNull(messagePayload) && nonNull(topic)) {
            messagePayload.setTopic(topic);
            payloadExecutor.execute(messagePayload, PayloadHandlerCommand.REMOVE);
        }
    }

    @MessageMapping(WS_HEALTH)
    public void healthCheck(@Payload HealthStatus healthStatus, Principal principal) {
        if (nonNull(healthStatus) && nonNull(principal)) {
            healthStatus.setClientId(principal.getName());
            payloadExecutor.execute(healthStatus, PayloadHandlerCommand.HEALTH_CHECK);
        }
    }
}
