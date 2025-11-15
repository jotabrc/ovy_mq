package io.github.jotabrc.ovy_mq_core.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ClientType {

    CONSUMER,
    PRODUCER,
    CONFIGURER,
    SERVER;
}
