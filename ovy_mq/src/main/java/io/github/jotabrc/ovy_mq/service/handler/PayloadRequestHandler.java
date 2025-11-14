package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.security.SecurityHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Mapping;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_core.domain.MessageStatus;
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
        log.info("Message request: client={}", client.getId());
        MessagePayload messagePayload = messageRepository.pollFromQueue(client.getTopicForAwaitingProcessingQueue());
        if (nonNull(messagePayload) && nonNull(client.getId())) sendMessageToClient(client, messagePayload);
    }

    private void sendMessageToClient(Client client, MessagePayload messagePayload) {
        log.info("Message request: message={} client={}", messagePayload.getId(), client.getId());
        try {
            sendMessageToConsumer(messagePayload, client);
            messagePayload.updateMessageStatusTo(MessageStatus.SENT);
        } catch (Exception e) {
            log.warn("Error while sending message: requeue message={} client={} topic={}:{}. Error-message: {}", messagePayload.getId(), client.getId(), messagePayload.getTopic(), messagePayload.getMessageStatus(), e.getMessage(), e);
        } finally {
            messageRepository.saveToQueue(messagePayload);
        }
    }

    private void sendMessageToConsumer(MessagePayload messagePayload, Client client) {
        log.info("Sending message={} client={} topic={} created-at={}", messagePayload.getId(), client.getId(), client.getTopic(), messagePayload.getCreatedDate());
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
        accessor.setNativeHeader(Key.HEADER_AUTHORIZATION, securityHeader.get(Key.HEADER_AUTHORIZATION).toString());
        accessor.setNativeHeader(Key.HEADER_PAYLOAD_TYPE, Value.PAYLOAD_TYPE_MESSAGE_PAYLOAD);
        return accessor.getMessageHeaders();
    }

    @Override
    public Class<Client> supports() {
        return Client.class;
    }

    @Override
    public PayloadDispatcherCommand command() {
        return PayloadDispatcherCommand.REQUEST;
    }
}
