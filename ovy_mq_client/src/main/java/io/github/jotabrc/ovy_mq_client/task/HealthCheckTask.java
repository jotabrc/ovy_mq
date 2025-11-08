package io.github.jotabrc.ovy_mq_client.task;

import io.github.jotabrc.ovy_mq_client.service.components.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionInitializer;
import io.github.jotabrc.ovy_mq_client.service.registry.provider.ClientRegistryProvider;
import io.github.jotabrc.ovy_mq_client.service.registry.provider.ClientSessionRegistryProvider;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.HealthStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.REQUEST_HEALTH_CHECK;

@Slf4j
@Component
@ConditionalOnProperty(
        name = "ovymq.task.health-check.active",
        havingValue = "true",
        matchIfMissing = false
)
public class HealthCheckTask {

    private final SessionInitializer sessionInitializer;
    private final ClientRegistryProvider clientRegistryProvider;
    private final ClientSessionRegistryProvider clientSessionRegistryProvider;
    private final ClientMessageDispatcher clientMessageDispatcher;

    private final Long delay;
    private final Long threshold;

    public HealthCheckTask(SessionInitializer sessionInitializer,
                           ClientRegistryProvider clientRegistryProvider,
                           ClientSessionRegistryProvider clientSessionRegistryProvider,
                           ClientMessageDispatcher clientMessageDispatcher,
                           @Value("${ovymq.task.health-check.delay}") Long delay,
                           @Value("${ovymq.task.health-check.threshold}") Long threshold) {
        this.sessionInitializer = sessionInitializer;
        this.clientRegistryProvider = clientRegistryProvider;
        this.clientSessionRegistryProvider = clientSessionRegistryProvider;
        this.clientMessageDispatcher = clientMessageDispatcher;
        this.delay = delay;
        this.threshold = threshold;
    }

    @Scheduled(fixedDelayString = "${ovymq.task.health-check.delay}")
    public void execute() {
        log.info("Health check task execution started, delay={} and threshold={}", delay, threshold);
        clientRegistryProvider.getAllClients()
                .forEach(client -> {
                    log.info("Request health check: client={} topic={} last health check={}", client.getId(), client.getTopic(), client.getLastHealthCheck());
                    clientSessionRegistryProvider.getById(client.getId())
                            .ifPresent(session -> {
                                session.reconnectIfNotAlive(isLastHealthCheckExpired(client));
                                HealthStatus healthStatus = buildHealthStatus();
                                clientMessageDispatcher.send(client, client.getTopic(), REQUEST_HEALTH_CHECK, healthStatus, session);
                            });
                });
    }

    private boolean isLastHealthCheckExpired(Client client) {
        return OffsetDateTime.now().minus(threshold, ChronoUnit.MILLIS).isAfter(client.getLastHealthCheck());
    }

    private static HealthStatus buildHealthStatus() {
        return HealthStatus.builder()
                .requestedAt(OffsetDateTime.now())
                .alive(false)
                .build();
    }
}