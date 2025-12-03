package io.github.jotabrc.ovy_mq_core.domain.client;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ListenerConfig {

    private String topic;
    private Replica replica;
    private Long processingTimeout;
    private Long pollInitialDelay;
    private Long pollFixedDelay;
    private Long healthCheckInitialDelay;
    private Long healthCheckFixedDelay;
    private Long healthCheckExpirationTime;
    private Integer connectionMaxRetries;
    private Long connectionTimeout;
    private Boolean useGlobalValues;
}
