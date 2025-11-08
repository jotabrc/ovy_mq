package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.WS_CONFIG;
import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.WS_LISTENER;

@RestController
@RequestMapping(WS_LISTENER)
public class ListenerController {

    @PostMapping(WS_CONFIG)
    public void configureListener(@RequestBody ListenerConfig listenerConfig) {
        /*
        TODO
        receive config and send to related clients
         */
    }
}
