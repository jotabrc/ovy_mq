package io.github.jotabrc.ovy_mq.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
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
    private final ObjectMapper objectMapper;

    @Override
    public void handle(OvyAction ovyAction) {
        MessagePayload messagePayload = ovyAction.getPayloadAs(MessagePayload.class, objectMapper);
        if (nonNull(messagePayload) && messagePayload.hasIdentifiers()) {
            log.info("Removing message={} topic={}", messagePayload.getId(), messagePayload.getTopicKey());
            messageRepository.removeFromQueue(messagePayload.getTopicKey(), messagePayload.getId());
        }
    }

    @Override
    public OvyCommand command() {
        return OvyCommand.REMOVE_MESSAGE_PAYLOAD;
    }
}
