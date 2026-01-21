package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcher;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.Optional;

import static io.github.jotabrc.ovy_mq_core.constants.Mapping.WS_CLIENT;

@AllArgsConstructor
@Controller
public class MessageController {

    private final PayloadDispatcher payloadDispatcher;

    @MessageMapping(WS_CLIENT)
    public void process(@Payload OvyAction ovyAction) {
        Optional.ofNullable(ovyAction)
                .ifPresent(action -> payloadDispatcher.execute(ovyAction));
    }
}
