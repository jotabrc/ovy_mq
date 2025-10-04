package io.github.jotabrc.ovy_mq_client.domain;

import io.github.jotabrc.ovy_mq_client.service.domain.client.ClientSession;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;

@Setter
@Builder
@Getter
public class Client {

    private String id;
    private String topic;
    private Boolean isAvailable;
    private OffsetDateTime lastUsed;
    private ClientType type;
    private Long replicas;
    private Long replicasInUse;
    private Method method;
    private ClientSession clientSession;
}
