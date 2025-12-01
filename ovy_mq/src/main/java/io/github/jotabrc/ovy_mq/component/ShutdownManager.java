package io.github.jotabrc.ovy_mq.component;

import io.github.jotabrc.ovy_mq.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class ShutdownManager implements ApplicationListener<ContextClosedEvent> {

    private final MessageRepository messageRepository;

    @Value("${ovymq.task.shutdown.wait-delay:1000}")
    private Long waitDelay;

    @Value("${ovymq.task.shutdown.max-wait:180000}")
    private Long maxWait;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        execute();
    }

    private void execute() {
        log.info("Executing graceful shutdown");
        long startTime = System.currentTimeMillis();
        while (!Objects.equals(0, messageRepository.getAwaitingConfirmationQuantity())) {
            try {
                if (System.currentTimeMillis() - startTime > maxWait) {
                    log.info("Waiting graceful shutdown time exceeded {} ms", maxWait);
                    break;
                }
                log.info("Awaiting messages={} to receive confirmation", messageRepository.getAwaitingConfirmationQuantity());
                Thread.sleep(waitDelay);
            } catch (InterruptedException e) {
                log.error("Error while executing graceful shutdown", e);
            }
        }
        log.info("Shutdown completed");
    }
}
