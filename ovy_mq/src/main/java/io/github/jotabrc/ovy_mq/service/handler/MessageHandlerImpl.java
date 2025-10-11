package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.MessageStatus;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageHandler;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.QueueHandler;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

import static java.util.Objects.isNull;

@AllArgsConstructor
@Service
public class MessageHandlerImpl implements MessageHandler {

    private final QueueHandler queueHandler;

    @Async
    @Override
    public void processAndSave(MessagePayload message) {
        updateMessageMetadata(message);
        queueHandler.save(message);
    }

    private void updateMessageMetadata(MessagePayload message) {
        message.updateMessageStatusTo(MessageStatus.AWAITING_PROCESSING);
        if (isNull(message.getId())) message.updateMessageMetadata(UUID.randomUUID().toString(), OffsetDateTime.now());
    }

    @Override
    public void removeFromProcessingQueue(String topic, String messageId) {
        queueHandler.remove(topic, messageId);
    }
}
