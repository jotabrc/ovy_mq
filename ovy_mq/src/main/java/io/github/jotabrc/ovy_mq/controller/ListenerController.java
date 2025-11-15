package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcher;
import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcherCommand;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.WS_CONFIG;
import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.WS_LISTENER;
import static java.util.Objects.nonNull;

@RestController
@RequiredArgsConstructor
@RequestMapping(WS_LISTENER)
public class ListenerController {

    private final PayloadDispatcher payloadDispatcher;

    @PostMapping(WS_CONFIG)
    public void configureListener(@RequestBody ListenerConfig listenerConfig) {
        if (nonNull(listenerConfig)) {
            payloadDispatcher.execute(listenerConfig, PayloadDispatcherCommand.LISTENER_CONFIG);
        }
    }
}
