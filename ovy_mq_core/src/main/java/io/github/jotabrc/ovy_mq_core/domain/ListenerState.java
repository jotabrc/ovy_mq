package io.github.jotabrc.ovy_mq_core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@Builder
public class ListenerState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String topic;
    private Integer replicas;
    private Integer maxReplicas;
    private Integer minReplicas;
    private Integer stepReplicas;
    private boolean autoManageReplicas;
    private Long timeout;
}
