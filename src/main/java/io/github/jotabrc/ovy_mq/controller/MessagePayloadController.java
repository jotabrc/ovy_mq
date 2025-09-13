package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.security.SecurityHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessagePayloadController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SecurityHandler securityHandler;

    public MessagePayloadController(SimpMessagingTemplate messagingTemplate,
                                    SecurityHandler securityHandler) {
        this.messagingTemplate = messagingTemplate;
        this.securityHandler = securityHandler;
    }

    @MessageMapping("/queue")
    public void msg(@Payload MessagePayload messagePayload, Principal principal) {

        // enviando mensagem para /send/messagePayload.getTopic()
        messagingTemplate.convertAndSendToUser(principal.getName(),
                "/queue/" + messagePayload.getTopic(),
                messagePayload.getPayload(),
                securityHandler.createAuthorizationHeader());
    }
}
