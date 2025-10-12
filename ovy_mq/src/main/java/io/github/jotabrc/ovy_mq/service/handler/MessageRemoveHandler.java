package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.MessageRecord;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageRemoveHandler implements MessageHandler {

    private final MessageRepository messageRepository;

    @Override
    public MessageRecord handle(MessageRecord messageRecord) {
        MessagePayload messagePayload = messageRecord.getMessagePayload();
        if (nonNull(messagePayload) && messagePayload.hasIdentifiers()) {
            log.info("Handling message={} removal", messagePayload.getId());
            messageRepository.removeFromProcessingQueue(messagePayload.getTopic(), messagePayload.getId());
        }
        return null;
    }
}