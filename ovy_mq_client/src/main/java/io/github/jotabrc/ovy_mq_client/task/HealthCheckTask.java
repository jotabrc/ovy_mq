package io.github.jotabrc.ovy_mq_client.task;

import io.github.jotabrc.ovy_mq_client.service.ClientMessageSender;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientSessionInitializerHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_client.service.registry.ClientSessionRegistryProvider;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.HealthStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@ConditionalOnProperty(
        name = "ovymq.task.health-check.active",
        havingValue = "true",
        matchIfMissing = false
)
public class HealthCheckTask {

    private final ClientSessionInitializerHandler clientSessionInitializerHandler;
    private final ClientRegistry clientRegistry;
    private final ClientSessionRegistryProvider clientSessionRegistryProvider;
    private final ClientMessageSender clientMessageSender;

    private final Long delay;
    private final Long threshold;

    public HealthCheckTask(ClientSessionInitializerHandler clientSessionInitializerHandler,
                           ClientRegistry clientRegistry,
                           ClientSessionRegistryProvider clientSessionRegistryProvider,
                           ClientMessageSender clientMessageSender,
                           @Value("${ovymq.task.health-check.delay}") Long delay,
                           @Value("${ovymq.task.health-check.threshold}") Long threshold) {
        this.clientSessionInitializerHandler = clientSessionInitializerHandler;
        this.clientRegistry = clientRegistry;
        this.clientSessionRegistryProvider = clientSessionRegistryProvider;
        this.clientMessageSender = clientMessageSender;
        this.delay = delay;
        this.threshold = threshold;
    }

    @Scheduled(fixedDelayString = "${ovymq.task.health-check.delay}")
    public void execute() {
        log.info("Health check task execution started, delay={} and threshold={}", delay, threshold);
        clientRegistry.getAllClients()
                .forEach(client -> {
                    log.info("Request health check: client={} topic={} last health check={}", client.getId(), client.getTopic(), client.getLastHealthCheck());
                    clientSessionRegistryProvider.getById(client.getId())
                            .ifPresent(session -> {
                                if (isLastHealthCheckExpired(client) || !session.isConnected()) {
                                    session.disconnect();
                                    clientSessionInitializerHandler.initialize(client);
                                } else {
                                    HealthStatus healthStatus = buildHealthStatus();
                                    clientMessageSender.send(client, client.getTopic(), client.requestHealthCheck(), healthStatus, session);
                                }
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