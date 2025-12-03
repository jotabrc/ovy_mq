package io.github.jotabrc.ovy_mq_core.domain.client;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClientType {

    CONSUMER,
    CONSUMER_MESSAGE_REQUEST_BASIC,
    PRODUCER,
    CONFIGURER,
    SERVER;
}
