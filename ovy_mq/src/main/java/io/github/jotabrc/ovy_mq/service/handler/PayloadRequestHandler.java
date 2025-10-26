package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.config.Mapping;
import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.MessageStatus;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.security.SecurityHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
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
public class PayloadRequestHandler implements PayloadHandler<Client> {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SecurityHandler securityHandler;

    @Override
    public void handle(Client client) {
        log.info("Handling message request for client={}", client.getId());
        MessagePayload messagePayload = messageRepository.removeFromQueueAndReturn(client.getTopicForAwaitingProcessingQueue());
        if (nonNull(messagePayload) && nonNull(client.getId())) sendMessageToClient(client, messagePayload);
    }

    private void sendMessageToClient(Client client, MessagePayload messagePayload) {
        log.info("Found message={} for client={}", messagePayload.getId(), client.getId());
        try {
            sendMessageToConsumer(messagePayload, client);
        } catch (Exception e) {
            log.warn("Message={} not sent to client={} on topic={}. Saving message in AWAITING_PROCESSING queue. Error: {}", messagePayload.getId(), client.getId(), messagePayload.getTopic(), e.getMessage(), e);
        } finally {
            messagePayload.updateMessageStatusTo(MessageStatus.SENT);
            messageRepository.saveToQueue(messagePayload);
        }
    }

    private void sendMessageToConsumer(MessagePayload messagePayload, Client client) {
        log.info("Sending message={} to client={} with topic={} created-at={}", messagePayload.getId(), client.getId(), client.getTopic(), messagePayload.getCreatedDate());
        messagePayload.setTopic(client.getTopic());
        messagingTemplate.convertAndSendToUser(client.getId(),
                createDestination(client.getTopic()),
                messagePayload,
                createHeaders());
    }

    private String createDestination(String topic) {
        return Mapping.WS_QUEUE + "/" + topic;
    }

    private MessageHeaders createHeaders() {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        accessor.setLeaveMutable(true);
        Map<String, Object> securityHeader = securityHandler.createAuthorizationHeader();
        accessor.setNativeHeader("Authorization", securityHeader.get("Authorization").toString());
        accessor.setNativeHeader("payload-type", "message-payload");
        return accessor.getMessageHeaders();
    }

    @Override
    public Class<Client> supports() {
        return Client.class;
    }

    @Override
    public PayloadHandlerCommand command() {
        return PayloadHandlerCommand.REQUEST;
    }
}
