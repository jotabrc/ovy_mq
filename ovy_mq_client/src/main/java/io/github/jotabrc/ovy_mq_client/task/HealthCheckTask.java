package io.github.jotabrc.ovy_mq_client.task;

import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientSessionInitializerHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
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
    private final Long delay;
    private final Long threshold;

    public HealthCheckTask(ClientSessionInitializerHandler clientSessionInitializerHandler,
                           ClientRegistry clientRegistry,
                           @Value("${ovymq.task.health-check.delay}") Long delay,
                           @Value("${ovymq.task.health-check.threshold}") Long threshold) {
        this.clientSessionInitializerHandler = clientSessionInitializerHandler;
        this.clientRegistry = clientRegistry;
        this.delay = delay;
        this.threshold = threshold;
    }

    @Scheduled(fixedDelayString = "${ovymq.task.health-check.delay}")
    public void execute() {
        log.info("Health check task execution started, delay={} and threshold={}", delay, threshold);
        clientRegistry.getAllClients()
                .forEach(client -> {
                    log.info("Request health check for client={} listening to topic={}", client.getId(), client.getTopic());
                    if (OffsetDateTime.now().minus(threshold, ChronoUnit.MILLIS).isAfter(client.getLastHealthCheckResponse())
                            || !client.getClientSessionHandler().getSession().isConnected()) {
                        if (client.getClientSessionHandler().getSession().isConnected()) {
                            client.getClientSessionHandler().getSession().disconnect();
                        }
                        clientSessionInitializerHandler.initialize(client);
                        client.setLastHealthCheckResponse(OffsetDateTime.now());
                    } else {
                        client.requestHealthCheck();
                    }
                });
        // TODO Health Check component
    }
}