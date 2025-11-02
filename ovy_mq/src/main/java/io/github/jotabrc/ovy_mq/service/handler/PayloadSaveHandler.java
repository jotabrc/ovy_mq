package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.defaults.MessageStatus;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadSaveHandler implements PayloadHandler<MessagePayload> {

    private final MessageRepository messageRepository;

    @Override
    public void handle(MessagePayload messagePayload) {
        updateMessageMetadata(messagePayload);
        log.info("Saving message={} topic={}", messagePayload.getId(), messagePayload.getTopic());
        messageRepository.saveToQueue(messagePayload);
    }

    private void updateMessageMetadata(io.github.jotabrc.ovy_mq.domain.MessagePayload message) {
        message.updateMessageStatusTo(MessageStatus.AWAITING_PROCESSING);
        if (isNull(message.getId())) message.updateMessageMetadata(UUID.randomUUID().toString(), OffsetDateTime.now());
    }

    @Override
    public Class<MessagePayload> supports() {
        return MessagePayload.class;
    }

    @Override
    public PayloadDispatcherCommand command() {
        return PayloadDispatcherCommand.SAVE;
    }
}
