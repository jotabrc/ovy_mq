package io.github.jotabrc.ovy_mq.service.task;

import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.QueueHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
@ConditionalOnProperty(
        name = "ovymq.task.consumer.auto",
        havingValue = "true",
        matchIfMissing = false
)
public class ConsumerTaskExecutor {

    private final ClientRegistryHandler clientRegistryHandler;
    private QueueHandler queueHandler;

    @Scheduled(fixedDelayString = "${ovymq.task.consumer.delay}")
    public void execute() {
        log.info("Executing task for consumers, initializing...");
        clientRegistryHandler.findAllAvailableClients().forEach(client -> {
                    log.info("Searching message for client {} listening for topic {}", client.getId(), client.getListeningTopic());
                    queueHandler.getMessageByTopic(client.getListeningTopic()).forEach(message -> {
                                log.info("Found message {} in topic {} for client {}", message.getId(), message.getTopic(), client.getId());
                                queueHandler.send(client, message);
                            }
                    );
                }
        );
    }
}
