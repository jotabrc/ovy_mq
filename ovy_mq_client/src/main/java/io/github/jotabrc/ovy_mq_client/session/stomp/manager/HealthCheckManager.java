package io.github.jotabrc.ovy_mq_client.session.stomp.manager;

import io.github.jotabrc.ovy_mq_client.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.payload.HealthStatus;
import io.github.jotabrc.ovy_mq_core.util.ValueUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static io.github.jotabrc.ovy_mq_core.constants.Mapping.REQUEST_HEALTH_CHECK;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HealthCheckManager extends AbstractManager {

    private final ClientMessageDispatcher clientMessageDispatcher;
    private final ScheduledExecutorService scheduledExecutor;

    @Value("${ovymq.task.health-check.initial.delay:10000}")
    private Long initialDelay;
    @Value("${ovymq.task.health-check.fixed-delay:60000}")
    private Long fixedDelay;
    @Value("${ovymq.task.health-check.expiration-time:120000}")
    private Long expirationTime;

    @Override
    public ScheduledFuture<?> execute() {
        scheduledFuture = scheduledExecutor.scheduleWithFixedDelay(() -> {
                    log.info("Executing health check: {}", client.getId());
                    reconnectIfNeeded(isLastHealthCheckExpired(this.client));
                    HealthStatus healthStatus = buildHealthStatus();
                    clientMessageDispatcher.send(this.client, this.client.getTopic(), REQUEST_HEALTH_CHECK, healthStatus, this.sessionManager);
                }, ValueUtil.get(client.getHealthCheckInitialDelay(), this.initialDelay, client.useGlobalValues()),
                ValueUtil.get(client.getHealthCheckFixedDelay(), this.fixedDelay, client.useGlobalValues()),
                TimeUnit.MILLISECONDS);
        return scheduledFuture;
    }

    private void reconnectIfNeeded(boolean force) {
        log.info("Session connection status: alive={} client={}", this.sessionManager.isConnected(), client.getId());
        if (!sessionManager.isConnected() || force) {
            this.sessionManager.disconnect();
            this.sessionManager.initializeSession();
        }
    }

    private boolean isLastHealthCheckExpired(Client client) {
        return OffsetDateTime.now().minus(ValueUtil.get(client.getHealthCheckExpirationTime(), this.expirationTime, client.useGlobalValues()),
                        ChronoUnit.MILLIS)
                .isAfter(client.getLastHealthCheck());
    }

    private static HealthStatus buildHealthStatus() {
        return HealthStatus.builder()
                .requestedAt(OffsetDateTime.now())
                .alive(false)
                .build();
    }
}