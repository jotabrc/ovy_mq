package io.github.jotabrc.ovy_mq.service.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jotabrc.ovy_mq.repository.interfaces.MessageRepository;
import io.github.jotabrc.ovy_mq.security.handler.AuthHandlerResolver;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.chain.ChainType;
import io.github.jotabrc.ovy_mq_core.constants.Mapping;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadRequestHandler implements PayloadHandler {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AuthHandlerResolver authHandlerResolver;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(OvyAction ovyAction) {
        Client client = ovyAction.getPayloadAs(Client.class, objectMapper);
        log.info("Message request: client={}", client.getId());
        if (nonNull(client.getId())) {
            messageRepository.pollFromQueue(client.getTopicForAwaitingProcessingQueue())
                    .ifPresent(messagePayload -> sendMessageToClient(client, messagePayload));
        }
    }

    private void sendMessageToClient(Client client, MessagePayload messagePayload) {
        log.info("Message request: message={} client={}", messagePayload.getId(), client.getId());
        try {
            sendMessageToConsumer(messagePayload, client);
            messagePayload.updateMessageStatusTo(MessageStatus.SENT);
        } catch (Exception e) {
            log.warn("Error while sending message: requeue message={} client={} topic={}:{}. Error-message: {}", messagePayload.getId(), client.getId(), messagePayload.getTopicKey(), messagePayload.getMessageStatus(), e.getMessage(), e);
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
        authHandlerResolver.get(ChainType.AUTH_BASE64)
                .ifPresent(authHandler -> {
                    accessor.setNativeHeader(OvyMqConstants.AUTHORIZATION, authHandler.createAuthorizationHeader().get(OvyMqConstants.AUTHORIZATION).toString());
                    accessor.setNativeHeader(OvyMqConstants.PAYLOAD_TYPE, OvyMqConstants.PAYLOAD_TYPE_MESSAGE_PAYLOAD);
                });
        return accessor.getMessageHeaders();
    }

    @Override
    public OvyCommand command() {
        return OvyCommand.REQUEST_MESSAGE_PAYLOAD;
    }
}
