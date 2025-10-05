package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.service.MessageProcessor;
import io.github.jotabrc.ovy_mq.service.QueueProcessor;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@AllArgsConstructor
@Controller
public class MessageController {

    private final MessageProcessor messageProcessor;
    private final QueueProcessor queueProcessor;

    @MessageMapping("/save")
    public void saveMessage(@Payload MessagePayload message) {
        messageProcessor.process(message);
    }

    @MessageMapping("/message")
    public void requestMessage(@Payload MessagePayload message, Principal principal) {
        queueProcessor.send(principal.getName());
    }

    @MessageMapping("/notify-and-request")
    public void notifyAndRequestMessage(@Payload MessagePayload message, Principal principal) {
        queueProcessor.send(principal.getName());
        messageProcessor.removeFromProcessingQueue(message);
    }
}
