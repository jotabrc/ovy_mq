package io.github.jotabrc.ovy_mq_client.session.manager_handler;

import io.github.jotabrc.ovy_mq_client.messaging.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionConnection;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManagerInitializer;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
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
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static io.github.jotabrc.ovy_mq_core.constants.Mapping.SEND_COMMAND_TO_SERVER;

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
                    reconnectWhenRequired(isLastHealthCheckExpired(this.client));
                    OvyAction ovyAction = OvyAction.builder()
                            .commands(List.of(OvyCommand.REQUEST_HEALTH_CHECK))
                            .payload(buildHealthStatus(client.getId()))
                            .build();
                    clientMessageDispatcher.send(this.client, this.client.getTopic(), SEND_COMMAND_TO_SERVER, ovyAction, this.sessionManager);
                },
                ValueUtil.get(client.getHealthCheckInitialDelay(), this.initialDelay, client.useGlobalValues()),
                ValueUtil.get(client.getHealthCheckFixedDelay(), this.fixedDelay, client.useGlobalValues()),
                TimeUnit.MILLISECONDS);
        return scheduledFuture;
    }

    private void reconnectWhenRequired(boolean force) {
        SessionConnection sessionConnection = (SessionConnection) this.sessionManager;
        SessionManagerInitializer sessionManagerInitializer = (SessionManagerInitializer) this.sessionManager;
        log.info("Session connection status: alive={} client={}", sessionConnection.isConnected(), client.getId());
        if (!sessionConnection.isConnected() || force) {
            sessionConnection.disconnect(force);
            sessionManagerInitializer.initializeSession();
        }
    }

    private boolean isLastHealthCheckExpired(Client client) {
        return OffsetDateTime.now().minus(ValueUtil.get(client.getHealthCheckExpirationTime(), this.expirationTime, client.useGlobalValues()),
                        ChronoUnit.MILLIS)
                .isAfter(client.getLastHealthCheck());
    }

    private HealthStatus buildHealthStatus(String clientId) {
        return HealthStatus.builder()
                .requestedAt(OffsetDateTime.now())
                .clientId(clientId)
                .build();
    }
}