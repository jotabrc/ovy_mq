package io.github.jotabrc.ovy_mq_core.domain;

import lombok.Builder;

@Builder
public record ThreadLock(String topic, String messageId, String clientId) {
}