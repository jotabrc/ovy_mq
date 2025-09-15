package io.github.jotabrc.ovy_mq.service.task;

import io.github.jotabrc.ovy_mq.service.ConsumerRegistry;
import io.github.jotabrc.ovy_mq.service.QueueProcessor;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@ConditionalOnProperty(
        name = "ovymq.task.consumer.active",
        havingValue = "true",
        matchIfMissing = false
)
public class ConsumerTaskExecutor {

    private final ConsumerRegistry consumerRegistry;
    private QueueProcessor queueProcessor;

    @Scheduled(fixedDelayString = "${ovymq.task.consumer.delay}")
    public void execute() {
        consumerRegistry.findAllAvailableConsumers().forEach(consumer ->
                queueProcessor.getMessageByTopic(consumer.getListeningTopic()).forEach(message ->
                        queueProcessor.send(consumer, message))
        );
    }
}
