package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.domain.Consumer;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.service.MessageProcessor;
import io.github.jotabrc.ovy_mq.service.QueueProcessor;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller
public class MessageController {

    private final MessageProcessor messageProcessor;
    private final QueueProcessor queueProcessor;

    @MessageMapping("/save")
    public void saveMessage(@Payload MessagePayload message) {
        messageProcessor.process(message);
    }

    @MessageMapping("/request")
    public void requestMessage(@Payload Consumer consumer) {
        queueProcessor.send(consumer);
    }
}
