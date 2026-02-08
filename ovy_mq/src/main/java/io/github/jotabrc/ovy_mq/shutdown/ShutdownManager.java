package io.github.jotabrc.ovy_mq.shutdown;

import io.github.jotabrc.ovy_mq.queue.repository.interfaces.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShutdownManager implements SmartLifecycle {

    private final MessageRepository messageRepository;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Value("${ovymq.task.shutdown.wait-delay:10000}")
    private Long waitDelay;
    @Value("${ovymq.task.shutdown.max-wait:180000}")
    private Long maxWait;

    @Override
    public void stop(Runnable callback) {
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
        SmartLifecycle.super.stop(callback);
    }

    @Override
    public void start() {
        isRunning.set(true);
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void stop() {

    }
}
