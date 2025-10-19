package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.domain.HealthStatus;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.factory.ClientFactory;
import io.github.jotabrc.ovy_mq.domain.factory.MessageRecordFactory;
import io.github.jotabrc.ovy_mq.service.HealthCheck;
import io.github.jotabrc.ovy_mq.service.handler.executor.MessageHandlerExecutor;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static io.github.jotabrc.ovy_mq.config.Mapping.*;
import static io.github.jotabrc.ovy_mq.service.handler.strategy.MessageRegistryStrategy.*;
import static java.util.Objects.nonNull;

@AllArgsConstructor
@Controller
public class MessageController {

    private final MessageHandlerExecutor messageHandlerExecutor;
    private final HealthCheck healthCheck;

    @MessageMapping(WS_SAVE)
    public void saveMessage(@Payload MessagePayload messagePayload) {
        messageHandlerExecutor.execute(SAVE, MessageRecordFactory.of(messagePayload));
    }

    @MessageMapping(WS_MESSAGE)
    public void requestMessage(String topic, Principal principal) {
        messageHandlerExecutor.execute(REQUEST, MessageRecordFactory.of(ClientFactory.of(principal.getName(), topic)));
    }

    @MessageMapping(WS_MESSAGE + WS_CONFIRM)
    public void confirmProcessing(@Payload MessagePayload messagePayload, @Header("Listening-Topic") String topic) {
        if (nonNull(messagePayload) && nonNull(topic)) {
            messagePayload.setTopic(topic);
            messageHandlerExecutor.execute(REMOVE, MessageRecordFactory.of(messagePayload));
            if (!messagePayload.isSuccess() && messagePayload.isProcessable()) {
                messageHandlerExecutor.execute(SAVE, MessageRecordFactory.of(messagePayload));
            }
        }
    }

    @MessageMapping(WS_HEALTH)
    public void healthCheck(@Payload HealthStatus healthStatus, Principal principal) {
        healthStatus.setRequestedFromClientId(principal.getName());
        healthCheck.confirmServerIsAlive(healthStatus);
    }
}
