package io.github.jotabrc.ovy_mq_client.component.resource;

import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.component.session.registry.SessionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShutdownClientComponent extends AbstractResourceUtil {

    public ShutdownClientComponent(SessionRegistry sessionRegistry) {
        super(sessionRegistry);
    }

    @Async
    public void stop(SessionManager sessionManager) {
        log.info("Executing graceful shutdown");
        sessionManager.destroy();
        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                if (sessionManager.canDisconnect()) {
                    log.info("Client ready to disconnect");
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

        sessionManager.disconnect();
        log.info("Shutdown completed");
    }
}
