package io.github.jotabrc.ovy_mq.service.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jotabrc.ovy_mq.queue.repository.interfaces.MessageRepository;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadSaveHandler implements PayloadHandler {

    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(OvyAction ovyAction) {
        MessagePayload messagePayload = ovyAction.getPayloadAs(MessagePayload.class, objectMapper);
        updateMessageMetadata(messagePayload);
        log.info("Saving message={} topic={}", messagePayload.getId(), messagePayload.getTopicKey());
        messageRepository.saveToQueue(messagePayload);
    }

    private void updateMessageMetadata(MessagePayload message) {
        message.updateMessageStatusTo(MessageStatus.AWAITING_PROCESSING);
        if (isNull(message.getId())) message.updateMessageMetadata(UUID.randomUUID().toString(), OffsetDateTime.now());
    }

    @Override
    public OvyCommand command() {
        return OvyCommand.SAVE_MESSAGE_PAYLOAD;
    }
}
