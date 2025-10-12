package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.MessageStatus;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageSaveHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@AllArgsConstructor
@Service
public class MessageSaveHandlerImpl implements MessageSaveHandler {

    private final MessageRepository messageRepository;

    @Override
    public MessagePayload handle(MessagePayload messagePayload) {
        updateMessageMetadata(messagePayload);
        log.info("Handling message save request with id={} in topic={}", messagePayload.getId(), messagePayload.getTopic());
        return messageRepository.saveToQueue(messagePayload);
    }

    private void updateMessageMetadata(MessagePayload message) {
        message.updateMessageStatusTo(MessageStatus.AWAITING_PROCESSING);
        if (isNull(message.getId())) message.updateMessageMetadata(UUID.randomUUID().toString(), OffsetDateTime.now());
    }
}
