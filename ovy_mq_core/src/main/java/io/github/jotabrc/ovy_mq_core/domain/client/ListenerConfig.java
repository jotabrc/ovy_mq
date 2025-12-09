package io.github.jotabrc.ovy_mq_core.domain.client;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListenerConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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