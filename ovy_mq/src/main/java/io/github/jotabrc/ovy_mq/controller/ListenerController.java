package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcher;
import io.github.jotabrc.ovy_mq_core.domain.client.ListenerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static io.github.jotabrc.ovy_mq_core.constants.Mapping.WS_CONFIG;
import static io.github.jotabrc.ovy_mq_core.constants.Mapping.WS_LISTENER;

@RestController
@RequiredArgsConstructor
@RequestMapping(WS_LISTENER)
public class ListenerController {

    private final PayloadDispatcher payloadDispatcher;

    @PostMapping(WS_CONFIG)
    public void configureListener(@RequestBody ListenerConfig listenerConfig, @RequestHeader("OVY-SUBSCRIBED-TOPIC") String destination) {
        payloadDispatcher.execute(listenerConfig, io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand.LISTENER_CONFIG);
    }
}
