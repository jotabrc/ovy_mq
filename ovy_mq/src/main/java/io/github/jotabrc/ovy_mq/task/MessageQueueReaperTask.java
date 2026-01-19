package io.github.jotabrc.ovy_mq.task;

import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
        name = "ovymq.task.reaper.active",
        havingValue = "true",
        matchIfMissing = false
)
public class MessageQueueReaperTask {

    private final PayloadDispatcher payloadDispatcher;
    private final Long delay;

    public MessageQueueReaperTask(PayloadDispatcher payloadDispatcher,
                                  @Value("${ovymq.task.reaper.delay}") Long delay) {
        this.payloadDispatcher = payloadDispatcher;
        this.delay = delay;
    }

    @Scheduled(fixedDelayString = "${ovymq.task.reaper.delay}")
    public void execute() {
        log.info("Reaper task execution started with fixed delay of {} ms", delay);
        payloadDispatcher.execute(delay, io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand.REAPER);
    }
}
