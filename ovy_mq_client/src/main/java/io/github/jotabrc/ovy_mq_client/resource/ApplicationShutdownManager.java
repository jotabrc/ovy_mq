package io.github.jotabrc.ovy_mq_client.resource;

import io.github.jotabrc.ovy_mq_client.resource.shutdown.ShutdownUtil;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.session.registry.SessionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class ApplicationShutdownManager extends ShutdownUtil implements SmartLifecycle {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public ApplicationShutdownManager(SessionRegistry sessionRegistry) {
        super(sessionRegistry);
    }


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
