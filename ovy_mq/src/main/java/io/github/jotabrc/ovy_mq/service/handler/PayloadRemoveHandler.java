package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadRemoveHandler implements PayloadHandler<MessagePayload> {

    private final MessageRepository messageRepository;

    @Override
    public void handle(MessagePayload messagePayload) {
        if (nonNull(messagePayload) && messagePayload.hasIdentifiers()) {
            log.info("Removing message={} topic={}", messagePayload.getId(), messagePayload.getTopic());
            messageRepository.removeFromQueue(messagePayload.getTopic(), messagePayload.getId());
        }
    }

    @Override
    public Class<MessagePayload> supports() {
        return MessagePayload.class;
    }

    @Override
    public PayloadDispatcherCommand command() {
        return PayloadDispatcherCommand.REMOVE;
    }
}
