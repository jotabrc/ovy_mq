package io.github.jotabrc.ovy_mq.service.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jotabrc.ovy_mq.queue.repository.interfaces.MessageRepository;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadReaperHandler implements PayloadHandler {

    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(OvyAction ovyAction) {
        Long ms = ovyAction.getPayloadAs(Long.class, objectMapper);
        log.info("Reaping all messages in SENT QUEUE for longer than={}ms without success confirmation.", ms);
        messageRepository.getMessagesByLastUsedDateGreaterThen(ms)
                .forEach(payload -> {
                    payload.updateMessageStatusTo(MessageStatus.AWAITING_PROCESSING);
                    log.info("Saving payload={} topic={} after {}ms without processing success confirmation",
                            payload.getId(), payload.getTopicKey(), payload.getMsSinceStartedProcessing());
                    messageRepository.removeAndRequeue(payload);
                });

    }

    @Override
    public OvyCommand command() {
        return OvyCommand.REAPER_MESSAGE_PAYLOAD;
    }
}
