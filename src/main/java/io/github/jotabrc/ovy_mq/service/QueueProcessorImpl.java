package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.TopicUtil;
import io.github.jotabrc.ovy_mq.config.TaskConfig;
import io.github.jotabrc.ovy_mq.domain.Consumer;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.MessageStatus;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.security.SecurityHandler;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@AllArgsConstructor
@Service
public class QueueProcessorImpl implements QueueProcessor {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SecurityHandler securityHandler;
    private final TaskConfig taskConfig;
    private final TopicRegistry topicRegistry;
    private final ConsumerRegistry consumerRegistry;

    @Async
    @Override
    public void save(MessagePayload message) {

        Consumer consumer = consumerRegistry.findLeastRecentlyUsedConsumerAvailableForTopic(message.getTopic());
        if (isNull(consumer)) {
            messageRepository.saveToQueue(message);
        } else {
            send(consumer, message);
        }

        if (taskConfig.useTopicRegistry()) {
            topicRegistry.save(message.createTopicKey());
        }
    }

    @Async
    @Override
    public void send(String clientId) {
        Consumer consumer = consumerRegistry.findConsumerByClientId(clientId);
        send(consumer);
    }

    @Async
    @Override
    public void send(Consumer consumer) {
        MessagePayload message = messageRepository.removeFromQueueAndReturn(TopicUtil.createTopicKeyForAwaitProcessingQueue(consumer.getListeningTopic()));
        send(consumer, message);
    }

    @Async
    @Override
    public void send(Consumer consumer, MessagePayload message) {
        if (sendMessageToConsumer(message, consumer)) {
            message.updateMessageStatusTo(MessageStatus.PROCESSING);
            messageRepository.saveToQueue(message);
            updateClientRegistry(consumer);
        } else {
            messageRepository.saveToQueue(message);
        }
    }

    private synchronized boolean sendMessageToConsumer(MessagePayload message, Consumer consumer) {
        if (consumer.getIsAvailable()) {
            messagingTemplate.convertAndSendToUser(consumer.getId(),
                    createDestination(consumer.getListeningTopic()),
                    message.getPayload(),
                    securityHandler.createAuthorizationHeader());
            return true;
        }
        return false;
    }

    private String createDestination(String topic) {
        return BrokerMapping.SEND_TO_CONSUMER + "/" + topic;
    }

    private void updateClientRegistry(Consumer consumer) {
        if (taskConfig.useRegistry()) {
            consumer.updateStatus();
            consumerRegistry.updateClientList(consumer);
        }
    }

    @Override
    public List<MessagePayload> getMessagesByTopic(String topic) {
        return getMessagesByTopic(topic, 1);
    }

    @Override
    public List<MessagePayload> getMessagesByTopic(String topic, int quantity) {
        return messageRepository.removeFromQueueAndReturnList(TopicUtil.createTopicKeyForAwaitProcessingQueue(topic), quantity);
    }

    @Async
    @Override
    public void remove(MessagePayload message) {
        messageRepository.removeFromProcessingQueue(message.createTopicKey());
    }
}
