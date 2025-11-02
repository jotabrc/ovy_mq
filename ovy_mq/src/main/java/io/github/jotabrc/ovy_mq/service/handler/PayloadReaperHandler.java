package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.defaults.MessageStatus;
import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PayloadReaperHandler implements PayloadHandler<Long> {

    private final MessageRepository messageRepository;

    @Override
    public void handle(Long ms) {
        log.info("Reaping all messages in SENT QUEUE for longer than={}ms without success confirmation.", ms);
        messageRepository.getMessagesByLastUsedDateGreaterThen(ms)
                .forEach(payload -> {
                    log.info("Saving payload={} topic={}:AWAITING_PROCESSING after {}ms without processing success confirmation",
                            payload.getId(), payload.getTopic(), payload.getMsSinceStartedProcessing());
                    payload.updateMessageStatusTo(MessageStatus.AWAITING_PROCESSING);
                    messageRepository.removeAndRequeue(payload);
                });

    }

    @Override
    public Class<Long> supports() {
        return Long.class;
    }

    @Override
    public PayloadDispatcherCommand command() {
        return PayloadDispatcherCommand.REAPER;
    }
}
