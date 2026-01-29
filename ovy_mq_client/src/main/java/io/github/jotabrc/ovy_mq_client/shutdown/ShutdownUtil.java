package io.github.jotabrc.ovy_mq_client.shutdown;

import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientHelper;
import io.github.jotabrc.ovy_mq_client.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientState;
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
    public <T, U, V> void stopThis(ClientAdapter<T, U, V> clientAdapter) {
        ClientHelper<T> clientHelper = clientAdapter.getClientHelper();;
        log.info("Executing graceful shutdown: client={}", clientHelper.getClientId());
        final long startTime = System.currentTimeMillis();
        ClientState<T, U, V> clientState = clientAdapter.getClientState();

        Callable<Boolean> callable = () -> {
            try {
                if (clientState.destroy(false)) {
                    log.info("Graceful shutdown completed: client={}", clientHelper.getClientId());
                    return true;
                }
                if (isMaxWaitExceeded(startTime)) {
                    log.info("Waiting graceful shutdown time exceeded {} ms: client={}", maxWait, clientHelper.getClientId());
                    clientState.destroy(true);
                    return false;
                }
                log.info("Waiting clients to shutdown elapsed-time={} sec: client={}", elapsedTime(startTime) / 1000, clientHelper.getClientId());
                Thread.sleep(waitDelay);
            } catch (InterruptedException e) {
                log.error("Error while executing graceful shutdown: client={}", clientHelper.getClientId(), e);
            }
            return false;
        };

        boolean isDone = false;
        while (!isDone && !isMaxWaitExceeded(startTime)) {
            try {
                isDone = callable.call();
            } catch (Exception e) {
                log.error("Error executing graceful shutdown");
                clientState.destroy(true);
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
