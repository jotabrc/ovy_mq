package io.github.jotabrc.ovy_mq_client.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotifyResult {

    SUCCESS(true),
    FAILURE(false);

    private final boolean status;
}
