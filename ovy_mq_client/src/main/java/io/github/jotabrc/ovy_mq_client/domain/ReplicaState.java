package io.github.jotabrc.ovy_mq_client.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@Builder
public class ReplicaState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String topic;
    private int replicas;
    private int maxReplicas;
    private int minReplicas;
    private int stepReplicas;
    private int autoManageReplicas;

}
