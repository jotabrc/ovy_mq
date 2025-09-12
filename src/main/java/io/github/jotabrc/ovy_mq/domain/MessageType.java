package io.github.jotabrc.ovy_mq.domain;

public enum MessageType {

    AWAITING_PROCESSING,
    PROCESSING,
    AWAITING_REPROCESSING,
    REPROCESSING;
}
