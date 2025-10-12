package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.MessageRecord;
import io.github.jotabrc.ovy_mq.domain.MessageStatus;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@AllArgsConstructor
@Service
public class MessageSaveHandler implements MessageHandler {

    private final MessageRepository messageRepository;

    @Override
    public MessageRecord handle(MessageRecord messageRecord) {
        MessagePayload messagePayload = messageRecord.getMessagePayload();
        updateMessageMetadata(messagePayload);
        log.info("Handling message save request with id={} in topic={}", messagePayload.getId(), messagePayload.getTopic());
        messagePayload = messageRepository.saveToQueue(messagePayload);
        messageRecord.setMessagePayload(messagePayload);
        return messageRecord;
    }

    private void updateMessageMetadata(MessagePayload message) {
        message.updateMessageStatusTo(MessageStatus.AWAITING_PROCESSING);
        if (isNull(message.getId())) message.updateMessageMetadata(UUID.randomUUID().toString(), OffsetDateTime.now());
    }
}
