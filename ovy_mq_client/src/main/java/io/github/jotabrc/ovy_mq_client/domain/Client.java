package io.github.jotabrc.ovy_mq_client.domain;

import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;

@Builder
@Getter
public class Client {

    private String id;
    private String listeningTopic;
    private Boolean isAvailable;
    private OffsetDateTime lastUsed;
    private ClientType type;
    private Long replicas;
    private Long replicasInUse;
    private Method method;
}
