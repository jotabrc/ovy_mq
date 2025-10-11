package io.github.jotabrc.ovy_mq_client.service.task;

import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientRegistryHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
        name = "ovymq.task.consumer.active",
        havingValue = "true",
        matchIfMissing = false
)
public class ConsumerTask {

    private final ClientRegistryHandler clientRegistryHandler;
    private final String delay;

    public ConsumerTask(ClientRegistryHandler clientRegistryHandler,
                        @Value("${ovymq.task.consumer.delay}") String delay) {
        this.clientRegistryHandler = clientRegistryHandler;
        this.delay = delay;
    }

    @Scheduled(fixedDelayString = "${ovymq.task.consumer.delay}")
    public void execute() {
        log.info("Consumer task execution started with fixed delay of {}ms", delay);
        clientRegistryHandler.getAllAvailableClients()
                .forEach(client -> {
                    log.info("Requesting message for client {} listening to topic {}", client.getId(), client.getTopic());
                    client.requestMessage();
                });
    }
}
