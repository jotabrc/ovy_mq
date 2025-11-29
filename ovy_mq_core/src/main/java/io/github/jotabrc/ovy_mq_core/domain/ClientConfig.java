package io.github.jotabrc.ovy_mq_core.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientConfig {

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
