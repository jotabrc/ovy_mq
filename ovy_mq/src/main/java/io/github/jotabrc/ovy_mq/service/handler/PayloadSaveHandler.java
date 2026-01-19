package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
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

    @Override
    public void handle(OvyAction ovyAction) {
        MessagePayload messagePayload = ovyAction.getDefinitionMap().extract(OvyMqConstants.OBJECT_MESSAGE_PAYLOAD, MessagePayload.class);
        updateMessageMetadata(messagePayload);
        log.info("Saving message={} topic={}", messagePayload.getId(), messagePayload.getTopic());
        messageRepository.saveToQueue(messagePayload);
    }

    private void updateMessageMetadata(MessagePayload message) {
        message.updateMessageStatusTo(MessageStatus.AWAITING_PROCESSING);
        if (isNull(message.getId())) message.updateMessageMetadata(UUID.randomUUID().toString(), OffsetDateTime.now());
    }

    @Override
    public io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand command() {
        return io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand.SAVE;
    }
}
