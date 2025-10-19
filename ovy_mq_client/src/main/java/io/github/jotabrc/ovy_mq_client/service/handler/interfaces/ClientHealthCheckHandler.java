package io.github.jotabrc.ovy_mq_client.service.handler.interfaces;

import io.github.jotabrc.ovy_mq_client.domain.HealthStatus;

public interface ClientHealthCheckHandler {

    void handle(String clientId, HealthStatus healthStatus);
}
