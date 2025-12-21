package io.github.jotabrc.ovy_mq_client.resource.shutdown;

import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.session.registry.SessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class ShutdownUtil {

    protected final SessionRegistry sessionRegistry;

    @Value("${ovymq.task.shutdown.wait-delay:10000}")
    protected Long waitDelay;
    @Value("${ovymq.task.shutdown.max-wait:180000}")
    protected Long maxWait;

    @Async
    public void stopThis(SessionManager sessionManager) {

        log.info("Executing graceful shutdown");
        log.info("Phase 1: destroying tasks and marking client for destruction");
        sessionManager.destroy();

        final long finalStartTime = System.currentTimeMillis();

        Callable<Boolean> callable = () -> {
            try {
                if (sessionManager.canDisconnect()) {
                    log.info("Client ready to disconnect");
                    log.info("Phase 2: disconnecting session");
                    sessionManager.disconnect();
                    log.info("Graceful shutdown completed");
                    return true;
                }
                if (isMaxWaitExceeded(finalStartTime)) {
                    log.info("Waiting graceful shutdown time exceeded {} ms", maxWait);
                    return true;
                }
                log.info("Waiting clients to shutdown: elapsed-time={} sec", elapsedTime(finalStartTime) / 1000);
                Thread.sleep(waitDelay);
            } catch (InterruptedException e) {
                log.error("Error while executing graceful shutdown", e);
            }
            return false;
        };

        boolean isDone = false;
        while (!isDone && !isMaxWaitExceeded(finalStartTime)) {
            try {
                isDone = callable.call();
            } catch (Exception e) {
                log.error("Error executing graceful shutdown");
                sessionManager.disconnect();
            }
        }
    }

    protected boolean isMaxWaitExceeded(long finalStartTime) {
        return elapsedTime(finalStartTime) > maxWait;
    }

    protected long elapsedTime(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
}
