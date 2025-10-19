package io.github.jotabrc.ovy_mq_client.service.task;

import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientSessionInitializerHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

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
    private final String delay;

    public HealthCheckTask(ClientSessionInitializerHandler clientSessionInitializerHandler,
                           ClientRegistry clientRegistry,
                           @Value("${ovymq.task.health-check.delay}") String delay) {
        this.clientSessionInitializerHandler = clientSessionInitializerHandler;
        this.clientRegistry = clientRegistry;
        this.delay = delay;
    }

    @Scheduled(fixedDelayString = "${ovymq.task.health-check.delay}")
    public void execute() {
        log.info("Health check task execution started with fixed delay of {} ms", delay);
        clientRegistry.getAllAvailableClients()
                .stream()
                .forEach(client -> {
                    log.info("Request health check for client={} listening to topic={}", client.getId(), client.getTopic());
                    if (OffsetDateTime.now().minusMinutes(1).isAfter(client.getLastHealthCheckResponse())) {
                        client.getClientSessionHandler().getSession().disconnect();
                        clientSessionInitializerHandler.initialize(client);
                    } else {
                        client.requestHealthCheck();
                    }
                });
    }
}