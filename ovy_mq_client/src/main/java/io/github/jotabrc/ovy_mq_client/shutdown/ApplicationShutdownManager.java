package io.github.jotabrc.ovy_mq_client.shutdown;

import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionConnection;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.registry.SessionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class ApplicationShutdownManager extends ShutdownUtil implements SmartLifecycle {

    public ApplicationShutdownManager(SessionRegistry sessionRegistry) {
        super(sessionRegistry);
    }

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Override
    public void stop(Runnable callback) {
        log.info("Executing graceful shutdown");
        final long startTime = System.currentTimeMillis();
        while (true) {
            try {
                for (SessionManager sessionManager : sessionRegistry.getAll().values()) {
                    SessionConnection sessionConnection = (SessionConnection) sessionManager;
                    boolean hasDisconnected = sessionConnection.destroy(false);
                    if (hasDisconnected) sessionRegistry.removeById(sessionManager.getClientId());
                }
                if (sessionRegistry.getAll().isEmpty()) {
                    log.info("All sessions disconnected");
                    break;
                }
                if (super.isMaxWaitExceeded(startTime)) {
                    log.info("Waiting graceful shutdown time exceeded {} ms", maxWait);
                    sessionRegistry.getAll().values()
                            .forEach(sessionManager -> ((SessionConnection) sessionManager).destroy(true));
                    break;
                }
                log.info("Waiting clients to shutdown: elapsed-time={} sec", super.elapsedTime(startTime) / 1000);
                Thread.sleep(waitDelay);
            } catch (InterruptedException e) {
                log.error("Error while executing graceful shutdown", e);
            }
        }

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
