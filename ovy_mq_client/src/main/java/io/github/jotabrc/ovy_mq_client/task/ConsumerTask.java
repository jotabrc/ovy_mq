package io.github.jotabrc.ovy_mq_client.task;

import io.github.jotabrc.ovy_mq_client.service.components.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_client.service.registry.provider.ClientRegistryProvider;
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

    private final ClientRegistryProvider clientRegistryProvider;
    private final ClientMessageDispatcher clientMessageDispatcher;

    private final String delay;

    public ConsumerTask(ClientRegistryProvider clientRegistryProvider,
                        ClientMessageDispatcher clientMessageDispatcher,
                        @Value("${ovymq.task.consumer.delay}") String delay) {
        this.clientRegistryProvider = clientRegistryProvider;
        this.clientMessageDispatcher = clientMessageDispatcher;
        this.delay = delay;
    }

    @Scheduled(fixedDelayString = "${ovymq.task.consumer.delay}")
    public void execute() {
        log.info("Consumer task execution started with fixed delay of {} ms", delay);
        clientRegistryProvider.getAllAvailableClients()
                .forEach(client -> {
                    log.info("Requesting message: client={} topic={}", client.getId(), client.getTopic());
                    clientMessageDispatcher.send(client, client.getTopic(), client.requestMessage(), client.getTopic());
                });
    }
}
