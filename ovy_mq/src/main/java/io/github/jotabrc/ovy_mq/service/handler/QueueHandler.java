package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.config.TaskConfig;
import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.MessageStatus;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.security.SecurityHandler;
import io.github.jotabrc.ovy_mq.service.BrokerMapping;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.TopicRegistryHandler;
import io.github.jotabrc.ovy_mq.util.TopicUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class QueueHandler implements io.github.jotabrc.ovy_mq.service.handler.interfaces.QueueHandler {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SecurityHandler securityHandler;
    private final TaskConfig taskConfig;
    private final TopicRegistryHandler topicRegistryHandler;
    private final ClientRegistryHandler clientRegistryHandler;

    @Async
    @Override
    public void save(MessagePayload message) {

        messageRepository.saveToQueue(message);

        if (taskConfig.useTopicRegistry()) {
            topicRegistryHandler.save(message.getTopicKey());
        }
    }

    @Async
    @Override
    public void send(String clientId) {
        log.info("Sending message for client: {}", clientId);
        Client client = clientRegistryHandler.findClientById(clientId);
        send(client);
    }

    @Async
    @Override
    public void send(Client client) {
        MessagePayload message = messageRepository.removeFromQueueAndReturn(TopicUtil.createTopicKeyForAwaitProcessing(client.getListeningTopic()));
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
            log.info("Sending client={}, topic={}, payload={}", client.getId(), createDestination(client.getListeningTopic()), message);
            messagingTemplate.convertAndSendToUser(client.getId(),
                    createDestination(client.getListeningTopic()),
                    message,
                    createHeaders());
            return true;
        }
        return false;
    }

    private String createDestination(String topic) {
        return BrokerMapping.SEND_MESSAGE_TO_CONSUMER + "/" + topic;
    }

    private MessageHeaders createHeaders() {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        accessor.setLeaveMutable(true);
        Map<String, Object> securityHeader = securityHandler.createAuthorizationHeader();
        accessor.setNativeHeader("Authorization", securityHeader.get("Authorization").toString());
        return accessor.getMessageHeaders();
    }

    private void updateClientRegistry(Client client) {
        if (taskConfig.useRegistry()) {
//            client.updateStatus();
            clientRegistryHandler.updateClientList(client);
        }
    }

    @Override
    public List<MessagePayload> getMessageByTopic(String topic) {
        return getMessageByTopic(topic, 1);
    }

    @Override
    public List<MessagePayload> getMessageByTopic(String topic, int quantity) {
        return messageRepository.removeFromQueueAndReturnList(TopicUtil.createTopicKeyForAwaitProcessing(topic), quantity);
    }

    @Async
    @Override
    public void remove(MessagePayload message) {
        messageRepository.removeFromProcessingQueue(message.getTopicKey());
    }
}
