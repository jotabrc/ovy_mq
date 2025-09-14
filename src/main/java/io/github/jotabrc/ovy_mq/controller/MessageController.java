package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.service.MessageProcessor;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller
public class MessageController {

    private final MessageProcessor messageProcessor;

    @MessageMapping("/server")
    public void msg(@Payload MessagePayload message) {
        messageProcessor.process(message);
    }
}
