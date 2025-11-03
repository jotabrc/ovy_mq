package io.github.jotabrc.ovy_mq.controller;

import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/listener")
public class ListenerController {

    @PostMapping("/config")
    public void configureListener(@RequestBody ListenerConfig listenerConfig) {

    }
}
