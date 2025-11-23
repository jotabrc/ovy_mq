package io.github.jotabrc.ovy_mq_client.task;

import io.github.jotabrc.ovy_mq_client.component.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_client.component.initialize.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_core.components.LockProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.REQUEST_MESSAGE;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(
        name = "ovymq.task.consumer.active",
        havingValue = "true",
        matchIfMissing = false
)
public class ConsumerTask {

    private final ClientRegistry clientRegistry;
    private final ClientMessageDispatcher clientMessageDispatcher;
    private final LockProcessor lockProcessor;

    @Value("${ovymq.task.consumer.delay}")
    private String delay;

    @Scheduled(fixedDelayString = "${ovymq.task.consumer.delay}", initialDelayString = "10000")
    public void execute() {
        log.info("Consumer task execution started with fixed delay of {} ms", delay);
        clientRegistry.getAllAvailableClients()
                .forEach(client -> {
                    synchronized (lockProcessor.getLockByTopicAndClientId(client.getTopic(), client.getId())) {
                        log.info("Requesting message: client={} topic={}", client.getId(), client.getTopic());
                        clientMessageDispatcher.send(client, client.getTopic(), REQUEST_MESSAGE, client.getTopic());
                    }
                });
    }
}
