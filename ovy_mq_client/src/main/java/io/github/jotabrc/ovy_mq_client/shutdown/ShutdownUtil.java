package io.github.jotabrc.ovy_mq_client.shutdown;

import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionConnection;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.registry.SessionRegistry;
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

        log.info("Executing graceful shutdown: client={}", sessionManager.getClientId());
        final long startTime = System.currentTimeMillis();
        SessionConnection sessionConnection = (SessionConnection) sessionManager;

        Callable<Boolean> callable = () -> {
            try {
                if (sessionConnection.destroy(false)) {
                    log.info("Graceful shutdown completed: client={}", sessionManager.getClientId());
                    return true;
                }
                if (isMaxWaitExceeded(startTime)) {
                    log.info("Waiting graceful shutdown time exceeded {} ms: client={}", maxWait, sessionManager.getClientId());
                    sessionConnection.destroy(true);
                    return false;
                }
                log.info("Waiting clients to shutdown elapsed-time={} sec: client={}", elapsedTime(startTime) / 1000, sessionManager.getClientId());
                Thread.sleep(waitDelay);
            } catch (InterruptedException e) {
                log.error("Error while executing graceful shutdown: client={}", sessionManager.getClientId(), e);
            }
            return false;
        };

        boolean isDone = false;
        while (!isDone && !isMaxWaitExceeded(startTime)) {
            try {
                isDone = callable.call();
            } catch (Exception e) {
                log.error("Error executing graceful shutdown");
                sessionConnection.destroy(true);
            }
        }
    }

    protected boolean isMaxWaitExceeded(long startTime) {
        return elapsedTime(startTime) > maxWait;
    }

    protected long elapsedTime(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
}
