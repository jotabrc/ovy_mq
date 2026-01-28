package io.github.jotabrc.ovy_mq_client.session.manager_handler;


import io.github.jotabrc.ovy_mq_client.messaging.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_client.session.interfaces.Manager;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientInitializer;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientState;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.stomp_handler.StompClientSessionHandler;
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
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

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
public class StompHealthCheckManager implements Manager<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> {

    private final ClientMessageDispatcher clientMessageDispatcher;
    private final ScheduledExecutorService scheduledExecutor;

    @Value("${ovymq.task.health-check.initial.delay:10000}")
    private Long initialDelay;
    @Value("${ovymq.task.health-check.fixed-delay:60000}")
    private Long fixedDelay;
    @Value("${ovymq.task.health-check.expiration-time:120000}")
    private Long expirationTime;

    @Override
    public ScheduledFuture<?> execute(Client client,
                                      ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientState,
                                      ClientInitializer<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientInitializer) {
        return scheduledExecutor.scheduleWithFixedDelay(() -> {
                    log.info("Executing health check: {}", client.getId());
                    reconnectWhenRequired(isLastHealthCheckExpired(client), client, clientState, clientInitializer);
                    OvyAction ovyAction = OvyAction.builder()
                            .commands(List.of(OvyCommand.REQUEST_HEALTH_CHECK))
                            .payload(buildHealthStatus(client.getId()))
                            .build();
                    clientMessageDispatcher.send(client, SEND_COMMAND_TO_SERVER, ovyAction);
                },
                ValueUtil.get(client.getHealthCheckInitialDelay(), initialDelay, client.useGlobalValues()),
                ValueUtil.get(client.getHealthCheckFixedDelay(), fixedDelay, client.useGlobalValues()),
                TimeUnit.MILLISECONDS);
    }

    private void reconnectWhenRequired(boolean force,
                                       Client client,
                                       ClientState<?, ?, ?> clientState,
                                       ClientInitializer<?, ?, ?> clientInitializer) {
        log.info("Session connection status: alive={} client={}", clientState.isConnected(), client.getId());
        if (!clientState.isConnected() || force) {
            clientState.disconnect(force);
            clientInitializer.initializeSession();
        }
    }

    private boolean isLastHealthCheckExpired(Client client) {
        return OffsetDateTime.now().minus(ValueUtil.get(client.getHealthCheckExpirationTime(), expirationTime, client.useGlobalValues()),
                        ChronoUnit.MILLIS)
                .isAfter(client.getLastHealthCheck());
    }

    private HealthStatus buildHealthStatus(String clientId) {
        return HealthStatus.builder()
                .requestedAt(OffsetDateTime.now())
                .clientId(clientId)
                .build();
    }

    @Override
    public ManagerFactory factory() {
        return ManagerFactory.STOMP_HEALTH_CHECK;
    }
}