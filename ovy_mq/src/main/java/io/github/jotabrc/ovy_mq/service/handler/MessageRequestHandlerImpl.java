package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.config.BrokerMapping;
import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.factory.ClientFactory;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.MessageStatus;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.security.SecurityHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistrySelectHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryUpsertHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageRequestHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.Map;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageRequestHandlerImpl implements MessageRequestHandler {

    private final MessageRepository messageRepository;
    private final ClientRegistryUpsertHandler registryUpsert;
    private final ClientRegistrySelectHandler registrySelect;
    private final SimpMessagingTemplate messagingTemplate;
    private final SecurityHandler securityHandler;

    public void handle(String clientId) {
        handle(ClientFactory.of(clientId));
    }

    @Override
    public void handle(Client client) {
        log.info("Handling request for message for client={}", client.getId());
        MessagePayload messagePayload = messageRepository.removeFromQueueAndReturn(client.getTopicForAwaitingProcessingQueue());
        if (nonNull(messagePayload) && nonNull(client.getId())) handle(client, messagePayload);
    }

    private void handle(Client client, MessagePayload messagePayload) {
        log.info("Found message={} for client={}", messagePayload.getId(), client.getId());
        try {
            sendMessageToConsumer(messagePayload, client);
        } catch (Exception e) {
            messageRepository.saveToQueue(messagePayload);
        }
    }

    private void sendMessageToConsumer(MessagePayload message, Client client) {
        log.info("Sending message={} for client={} with topic={} created at {}", message.getId(), client.getId(), client.getTopic(), message.getCreatedDate());
        message.setTopic(client.getTopic());
        messagingTemplate.convertAndSendToUser(client.getId(),
                createDestination(client.getTopic()),
                message,
                createHeaders());
        message.updateMessageStatusTo(MessageStatus.PROCESSING);
    }

    private String createDestination(String topic) {
        return BrokerMapping.SENDER_MESSAGE_TO_CLIENT + "/" + topic;
    }

    private MessageHeaders createHeaders() {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        accessor.setLeaveMutable(true);
        Map<String, Object> securityHeader = securityHandler.createAuthorizationHeader();
        accessor.setNativeHeader("Authorization", securityHeader.get("Authorization").toString());
        return accessor.getMessageHeaders();
    }
}
