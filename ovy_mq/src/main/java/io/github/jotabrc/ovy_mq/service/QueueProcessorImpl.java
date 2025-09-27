package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.util.TopicUtil;
import io.github.jotabrc.ovy_mq.config.TaskConfig;
import io.github.jotabrc.ovy_mq.domain.Client;
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

        Client client = consumerRegistry.findLeastRecentlyUsedConsumerAvailableForTopic(message.getTopic());
        if (isNull(client)) {
            messageRepository.saveToQueue(message);
        } else {
            send(client, message);
        }

        if (taskConfig.useTopicRegistry()) {
            topicRegistry.save(message.createTopicKey());
        }
    }

    @Async
    @Override
    public void send(String clientId) {
        Client client = consumerRegistry.findConsumerByClientId(clientId);
        send(client);
    }

    @Async
    @Override
    public void send(Client client) {
        MessagePayload message = messageRepository.removeFromQueueAndReturn(TopicUtil.createTopicKeyForAwaitProcessingQueue(client.getListeningTopic()));
        send(client, message);
    }

    @Async
    @Override
    public void send(Client client, MessagePayload message) {
        if (sendMessageToConsumer(message, client)) {
            message.updateMessageStatusTo(MessageStatus.PROCESSING);
            messageRepository.saveToQueue(message);
            updateClientRegistry(client);
        } else {
            messageRepository.saveToQueue(message);
        }
    }

    private synchronized boolean sendMessageToConsumer(MessagePayload message, Client client) {
        if (client.getIsAvailable()) {
            messagingTemplate.convertAndSendToUser(client.getId(),
                    createDestination(client.getListeningTopic()),
                    message.getPayload(),
                    securityHandler.createAuthorizationHeader());
            return true;
        }
        return false;
    }

    private String createDestination(String topic) {
        return BrokerMapping.SEND_TO_CONSUMER + "/" + topic;
    }

    private void updateClientRegistry(Client client) {
        if (taskConfig.useRegistry()) {
            client.updateStatus();
            consumerRegistry.updateClientList(client);
        }
    }

    @Override
    public List<MessagePayload> getMessageByTopic(String topic) {
        return getMessageByTopic(topic, 1);
    }

    @Override
    public List<MessagePayload> getMessageByTopic(String topic, int quantity) {
        return messageRepository.removeFromQueueAndReturnList(TopicUtil.createTopicKeyForAwaitProcessingQueue(topic), quantity);
    }

    @Async
    @Override
    public void remove(MessagePayload message) {
        messageRepository.removeFromProcessingQueue(message.createTopicKey());
    }
}
