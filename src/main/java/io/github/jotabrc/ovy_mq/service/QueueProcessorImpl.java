package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.TopicUtil;
import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.MessageStatus;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.security.SecurityHandler;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class QueueProcessorImpl implements QueueProcessor {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SecurityHandler securityHandler;

    @Async
    @Override
    public void save(MessagePayload message) {
        messageRepository.saveToQueue(message);
    }

    @Async
    @Override
    public void send(Client client) {
        MessagePayload message = messageRepository.removeFromQueueAndReturn(TopicUtil.createTopicKeyForAwaitProcessingQueue(client.getListeningTopic()));
        sendMessageToConsumer(message, client);
        message.updateMessageStatusTo(MessageStatus.PROCESSING);
        messageRepository.saveToQueue(message);
    }

    private void sendMessageToConsumer(MessagePayload message, Client client) {
        messagingTemplate.convertAndSendToUser(client.getId(),
                createDestination(client.getListeningTopic()),
                message.getPayload(),
                securityHandler.createAuthorizationHeader());
    }

    private String createDestination(String topic) {
        return BrokerMapping.SEND_TO_CONSUMER + "/" + topic;
    }
}
