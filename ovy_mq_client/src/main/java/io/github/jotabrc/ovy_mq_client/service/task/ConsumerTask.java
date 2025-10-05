package io.github.jotabrc.ovy_mq_client.service.task;

import io.github.jotabrc.ovy_mq_client.domain.ActionFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static io.github.jotabrc.ovy_mq_client.domain.Command.REQUEST_MESSAGES_FOR_ALL_AVAILABLE_CLIENTS;

@Slf4j
@AllArgsConstructor
@Component
@ConditionalOnProperty(
        name = "ovymq.task.consumer.active",
        havingValue = "true",
        matchIfMissing = false
)
public class ConsumerTask {

    @Scheduled(fixedDelayString = "${ovymq.task.consumer.delay}")
    public void execute() {
        log.info("Executing consumer task, requesting messages for available clients");
        ActionFactory.and().execute(REQUEST_MESSAGES_FOR_ALL_AVAILABLE_CLIENTS);
    }
}
