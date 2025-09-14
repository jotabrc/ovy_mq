package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.MessageStatus;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MessageProcessorImpl implements MessageProcessor {

    private final QueueProcessor queueProcessor;

    @Async
    @Override
    public void process(MessagePayload message) {
        updateMessageMetadata(message);
        queueProcessor.save(message);
    }

    private void updateMessageMetadata(MessagePayload message) {
        message.updateMessageStatusTo(MessageStatus.AWAITING_PROCESSING);
        message.updateMessageMetadata(UUID.randomUUID().toString(), OffsetDateTime.now());
    }

    @Override
    public void removeFromProcessingQueue(MessagePayload message) {
        queueProcessor.remove(message);
    }
}
