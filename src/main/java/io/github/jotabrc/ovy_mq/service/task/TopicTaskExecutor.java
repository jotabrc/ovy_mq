package io.github.jotabrc.ovy_mq.service.task;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.service.ConsumerRegistry;
import io.github.jotabrc.ovy_mq.service.QueueProcessor;
import io.github.jotabrc.ovy_mq.service.TopicRegistry;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Objects.nonNull;

@AllArgsConstructor
@Component
@ConditionalOnProperty(
        name = "ovymq.task.topic.active",
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
            Integer quantity = consumerRegistry.isThereAnyAvailableConsumerForTopic(topic);
            if (!Objects.equals(0, quantity)) {
                queueProcessor.getMessageByTopic(topic, quantity).forEach(message -> {
                    Client client = consumerRegistry.findLeastRecentlyUsedConsumerAvailableForTopic(topic);
                    if (nonNull(client)) {
                        queueProcessor.send(client, message);
                    }
                });
            }
        });
    }
}
