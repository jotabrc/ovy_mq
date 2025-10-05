package io.github.jotabrc.ovy_mq.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrokerMapping {

    REGISTER("/registry"),
    SEND_TO_CONSUMER("/queue"),
    RECEIVE_FROM_CONSUMER("/request");

    private final String route;
}
