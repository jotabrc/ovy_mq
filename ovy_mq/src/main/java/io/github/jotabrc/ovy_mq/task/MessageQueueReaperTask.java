package io.github.jotabrc.ovy_mq.task;

import io.github.jotabrc.ovy_mq.service.handler.PayloadExecutor;
import io.github.jotabrc.ovy_mq.service.handler.PayloadHandlerCommand;
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

    private final PayloadExecutor payloadExecutor;
    private final String delay;

    public MessageQueueReaperTask(PayloadExecutor payloadExecutor,
                                  @Value("${ovymq.task.reaper.delay}") String delay) {
        this.payloadExecutor = payloadExecutor;
        this.delay = delay;
    }

    @Scheduled(fixedDelayString = "${ovymq.task.reaper.delay}")
    public void execute() {
        log.info("Reaper task execution started with fixed delay of {} ms", delay);
        payloadExecutor.execute(Long.parseLong(delay), PayloadHandlerCommand.REAPER);
    }
}
