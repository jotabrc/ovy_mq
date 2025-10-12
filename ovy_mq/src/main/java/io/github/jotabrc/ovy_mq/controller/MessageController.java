package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.domain.ConfigPayload;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageRemoveHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageRequestHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageSaveHandler;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static io.github.jotabrc.ovy_mq.config.BrokerMapping.*;
import static java.util.Objects.nonNull;

@AllArgsConstructor
@Controller
public class MessageController {

    private final MessageSaveHandler messageSaveHandler;
    private final MessageRequestHandler messageRequestHandler;
    private final MessageRemoveHandler messageRemoveHandler;

    @MessageMapping(SAVE_MESSAGE)
    public void saveMessage(@Payload MessagePayload message) {
        messageSaveHandler.handle(message);
    }

    @MessageMapping(MESSAGE_REQUEST)
    public void requestMessage(@Payload(required = false) MessagePayload messagePayload, Principal principal) {
        messageRequestHandler.handle(principal.getName());
    }

    @MessageMapping(MESSAGE_PROCESSED)
    public void confirmProcessing(@Payload MessagePayload messagePayload, @Header("Listening-Topic") String topic) {
        if (nonNull(messagePayload) && nonNull(topic)) {
            messagePayload.setTopic(topic);
            messageRemoveHandler.handle(messagePayload);
            if (!messagePayload.isSuccess() && messagePayload.isProcessable()) {
                messageSaveHandler.handle(messagePayload);
            }
        }
    }

    @MessageMapping(HEALTH_CHECK)
    public void healthCheck() {

    }

    @MessageMapping(CONFIGURE)
    public void replyConfiguration(@Payload ConfigPayload configPayload, Principal principal) {

    }
}
