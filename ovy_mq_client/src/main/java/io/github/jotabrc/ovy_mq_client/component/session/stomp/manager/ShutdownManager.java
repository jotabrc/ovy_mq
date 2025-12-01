package io.github.jotabrc.ovy_mq_client.component.session.stomp.manager;

import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShutdownManager implements AbstractManager, ApplicationListener<ContextClosedEvent> {

    private final ScheduledExecutorService scheduledExecutor;

    @Setter
    private SessionManager sessionManager;
    @Setter
    private Client client;

    @Value("${ovymq.task.shutdown.wait-delay:1000}")
    private Long waitDelay;
    @Value("${ovymq.task.shutdown.max-wait:180000}")
    private Long maxWait;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        execute();
    }

    @Override
    public ScheduledFuture<?> execute() {
        log.info("Executing graceful shutdown");
        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                if (client.canDisconnect()) {
                    sessionManager.disconnect();
                }
                if (System.currentTimeMillis() - startTime > maxWait) {
                    log.info("Waiting graceful shutdown time exceeded {} ms", maxWait);
                    break;
                }
                Thread.sleep(waitDelay);
            } catch (InterruptedException e) {
                log.error("Error while executing graceful shutdown", e);
            }
        }
        log.info("Shutdown completed");
        return null;
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("Destroy method not supported for ShutdownManager");
    }
}