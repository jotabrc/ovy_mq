package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.domain.ConfigPayload;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.QueueHandler;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static io.github.jotabrc.ovy_mq.service.BrokerMapping.*;

@AllArgsConstructor
@Controller
public class MessageController {

    private final MessageHandler messageHandler;
    private final QueueHandler queueHandler;

    @MessageMapping(SAVE_MESSAGE_RECEIVED)
    public void saveMessage(@Payload MessagePayload message) {
        messageHandler.process(message);
    }

    @MessageMapping(RECEIVE_MESSAGE_REQUEST_FROM_CONSUMER)
    public void requestMessage(@Payload MessagePayload message, Principal principal) {
        queueHandler.send(principal.getName());
    }

    @MessageMapping(RECEIVE_CONFIG_FROM_CONSUMER)
    public void replyConfiguration(@Payload ConfigPayload configPayload, Principal principal) {

    }
}
