package io.github.jotabrc.ovy_mq.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultClientKey {

    CLIENT_ID("clientId"),
    CLIENT_ID_NOT_FOUND("CLIENT_ID_NOT_FOUND"),
    CLIENT_LISTENING_TOPIC("Listening-Topic"),
    CLIENT_LISTENING_TOPIC_NOT_FOUND("CLIENT_LISTENING_TOPIC_NOT_FOUND");

    private final String value;
}
