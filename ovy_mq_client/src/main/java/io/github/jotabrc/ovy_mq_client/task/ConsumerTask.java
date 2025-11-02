package io.github.jotabrc.ovy_mq_client.task;

import io.github.jotabrc.ovy_mq_client.service.ClientMessageSender;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
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

    private final ClientRegistry clientRegistry;
    private final ClientMessageSender clientMessageSender;

    private final String delay;

    public ConsumerTask(ClientRegistry clientRegistry,
                        ClientMessageSender clientMessageSender,
                        @Value("${ovymq.task.consumer.delay}") String delay) {
        this.clientRegistry = clientRegistry;
        this.clientMessageSender = clientMessageSender;
        this.delay = delay;
    }

    @Scheduled(fixedDelayString = "${ovymq.task.consumer.delay}")
    public void execute() {
        log.info("Consumer task execution started with fixed delay of {} ms", delay);
        clientRegistry.getAllAvailableClients()
                .forEach(client -> {
                    log.info("Requesting message for client={} listening to topic={}", client.getId(), client.getTopic());
                    clientMessageSender.send(client.requestMessage(), client);
                });
    }
}
