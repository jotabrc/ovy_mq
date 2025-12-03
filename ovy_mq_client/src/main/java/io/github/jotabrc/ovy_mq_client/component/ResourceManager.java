package io.github.jotabrc.ovy_mq_client.component;

import io.github.jotabrc.ovy_mq_client.component.initialize.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Component
public class ResourceManager implements SmartLifecycle {

    private final SessionRegistry sessionRegistry;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Value("${ovymq.task.shutdown.wait-delay:1000}")
    private Long waitDelay;
    @Value("${ovymq.task.shutdown.max-wait:180000}")
    private Long maxWait;

    @Override
    public void stop(Runnable callback) {
        sessionRegistry.getAll().values().forEach(SessionManager::destroy);
        log.info("Executing graceful shutdown");
        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                boolean allSessionsReadyToDisconnect = sessionRegistry.getAll().values().stream().allMatch(SessionManager::canDisconnect);
                if (allSessionsReadyToDisconnect) {
                    log.info("All sessions ready to disconnect");
                    break;
                }
                if (elapsedTime(startTime) > maxWait) {
                    log.info("Waiting graceful shutdown time exceeded {} ms", maxWait);
                    break;
                }
                log.info("Waiting clients to shutdown: elapsed-time={} sec", elapsedTime(startTime) / 1000);
                Thread.sleep(waitDelay);
            } catch (InterruptedException e) {
                log.error("Error while executing graceful shutdown", e);
            }
        }

        sessionRegistry.getAll().values().forEach(SessionManager::disconnect);
        log.info("Shutdown completed");
        isRunning.set(false);
        callback.run();
    }

    private static long elapsedTime(long startTime) {
        return System.currentTimeMillis() - startTime;
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
