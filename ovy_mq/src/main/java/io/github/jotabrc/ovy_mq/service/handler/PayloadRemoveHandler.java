package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadRemoveHandler implements PayloadHandler {

    private final MessageRepository messageRepository;

    @Override
    public void handle(OvyAction ovyAction) {
        MessagePayload messagePayload = ovyAction.getDefinitionMap().extract(OvyMqConstants.OBJECT_MESSAGE_PAYLOAD, MessagePayload.class);
        if (nonNull(messagePayload) && messagePayload.hasIdentifiers()) {
            log.info("Removing message={} topic={}", messagePayload.getId(), messagePayload.getTopic());
            messageRepository.removeFromQueue(messagePayload.getTopic(), messagePayload.getId());
        }
    }

    @Override
    public io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand command() {
        return io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand.REMOVE;
    }
}
