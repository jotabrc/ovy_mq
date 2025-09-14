package io.github.jotabrc.ovy_mq.service.task;

import io.github.jotabrc.ovy_mq.domain.Consumer;
import io.github.jotabrc.ovy_mq.service.ConsumerRegistry;
import io.github.jotabrc.ovy_mq.service.QueueProcessor;
import io.github.jotabrc.ovy_mq.service.TopicRegistry;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@AllArgsConstructor
@Component
@ConditionalOnProperty(
        name = "{ovymq.task.topic.active",
        havingValue = "true",
        matchIfMissing = false
)
public class TopicTaskExecutor {

    private final TopicRegistry topicRegistry;
    private final ConsumerRegistry consumerRegistry;
    private QueueProcessor queueProcessor;

    @Scheduled(fixedDelayString = "${ovymq.task.topic.delay}")
    public void execute() {
        topicRegistry.getTopicList().forEach(topic -> {
            Integer quantity = consumerRegistry.getAvailableConsumersForTopic(topic);
            if (!Objects.equals(0, quantity)) {
                queueProcessor.getMessagesByTopic(topic, quantity).forEach(message -> {
                    Consumer consumer = consumerRegistry.obtainLeastRecentlyUsedConsumerAvailable(topic);
                    queueProcessor.send(consumer, message);
                });
            }
        });
    }
}
