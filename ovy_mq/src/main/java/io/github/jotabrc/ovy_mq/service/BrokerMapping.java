package io.github.jotabrc.ovy_mq.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrokerMapping {

    REGISTER("/registry"),
    SEND_TO_CONSUMER("/topic"),
    RECEIVE_FROM_CONSUMER("/queue");

    private final String route;
}
