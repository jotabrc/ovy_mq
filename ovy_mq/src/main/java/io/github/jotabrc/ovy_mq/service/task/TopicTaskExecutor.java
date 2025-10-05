package io.github.jotabrc.ovy_mq.service.task;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.service.ConsumerRegistry;
import io.github.jotabrc.ovy_mq.service.QueueProcessor;
import io.github.jotabrc.ovy_mq.service.TopicRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

import static java.util.Objects.nonNull;

@Slf4j
@AllArgsConstructor
@Component
@ConditionalOnProperty(
        name = "ovymq.task.topic.auto",
        havingValue = "true",
        matchIfMissing = false
)
public class TopicTaskExecutor {

    private final TopicRegistry topicRegistry;
    private final ConsumerRegistry consumerRegistry;
    private QueueProcessor queueProcessor;

    @Scheduled(fixedDelayString = "${ovymq.task.topic.delay}")
    public void execute() {
        log.info("Executing task for topics, initialization...");
        Set<String> topics = topicRegistry.getTopicList();
        if (!topics.isEmpty()) {
            topicRegistry.getTopicList().forEach(topic -> {
                Integer quantity = consumerRegistry.isThereAnyAvailableConsumerForTopic(topic);
                log.info("TopicTaskExecutor: found {} consumers available for topic {}", quantity, topic);
                if (!Objects.equals(0, quantity)) {
                    queueProcessor.getMessageByTopic(topic, quantity).forEach(message -> {
                        log.info("Searching consumer for message {} in topic {}", message.getId(), message.getTopic());
                        Client client = consumerRegistry.findLeastRecentlyUsedConsumerAvailableForTopic(topic);
                        if (nonNull(client)) {
                            log.info("Consumer {} found for message {} in topic {}", client.getId(), message.getId(), message.getTopic());
                            queueProcessor.send(client, message);
                        } else
                            log.info("No consumer found for message {} in topic {}", message.getId(), message.getTopic());
                    });
                }
            });
        }
    }
}
