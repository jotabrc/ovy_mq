package io.github.jotabrc.ovy_mq.task;

import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcher;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(
        name = "ovymq.task.reaper.active",
        havingValue = "true",
        matchIfMissing = false
)
public class MessageQueueReaperTask {

    private final PayloadDispatcher payloadDispatcher;

    @Value("${ovymq.task.reaper.delay}")
    private Long delay;

    @Scheduled(fixedDelayString = "${ovymq.task.reaper.delay}")
    public void execute() {
        log.info("Reaper task execution started with fixed delay of {} ms", delay);
        OvyAction ovyAction = buildAction();
        payloadDispatcher.execute(ovyAction);
    }

    private OvyAction buildAction() {
        return OvyAction.builder()
                .commands(List.of(OvyCommand.REAPER_MESSAGE_PAYLOAD))
                .payload(delay)
                .build();
    }
}
