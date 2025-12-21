package io.github.jotabrc.ovy_mq_client.session.stomp.manager;

import io.github.jotabrc.ovy_mq_client.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_client.session.interfaces.ConnectionManager;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionTimeoutManager;
import io.github.jotabrc.ovy_mq_client.session.stomp.StompSessionHandler;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.util.ValueUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static io.github.jotabrc.ovy_mq_core.constants.Mapping.WS_REGISTRY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompClientSessionTimeoutManager implements SessionTimeoutManager {

    @Value("${ovymq.session-manager.connection.max-retries:10}")
    protected Integer maxRetries;

    @Value("${ovymq.session-manager.connection.timeout:10000}")
    protected Long timeout;

    private final ObjectProviderFacade objectProviderFacade;
    private final AbstractFactoryResolver abstractFactoryResolver;
    private final ConnectionManager<StompSession, WebSocketHttpHeaders, StompSessionHandler> stompConnectionManager;
    private final ScheduledExecutorService scheduledExecutor;

    @Override
    public CompletableFuture<SessionManager> execute(SessionManager sessionManager, Client client, CompletableFuture<SessionManager> finalFuture) {
        DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                .add(OvyMqConstants.DESTINATION, OvyMqConstants.DESTINATION_SERVER)
                .add(OvyMqConstants.SUBSCRIBED_TOPIC, client.getTopic())
                .add(OvyMqConstants.CLIENT_TYPE, client.getType().name())
                .add(OvyMqConstants.CLIENT_ID, client.getId());

        var headers = abstractFactoryResolver.create(definition, WebSocketHttpHeaders.class);

        if (headers.isPresent()) {
            Function<ConnectionManager<StompSession, WebSocketHttpHeaders, StompSessionHandler>, CompletableFuture<SessionManager>> connect = stompConnectionManager -> {
                if (!sessionManager.isConnected()) {
                    client.setLastHealthCheck(OffsetDateTime.now());
                    return stompConnectionManager.connect("ws://localhost:9090/" + WS_REGISTRY, headers.get(), (StompSessionHandler) sessionManager)
                            .thenApply(stompSession -> sessionManager);
                }
                return CompletableFuture.completedFuture(sessionManager);
            };

            return execute(connect, client, finalFuture);
        }
        return CompletableFuture.failedFuture(new IllegalStateException("Headers factory is not present"));
    }

    private CompletableFuture<SessionManager> execute(Function<ConnectionManager<StompSession, WebSocketHttpHeaders, StompSessionHandler>, CompletableFuture<SessionManager>> connect, Client client, CompletableFuture<SessionManager> finalFuture) {
        connect(finalFuture, connect, client, 1);
        return finalFuture;
    }

    @Override
    public SessionType supports() {
        return SessionType.STOMP;
    }

    private void connect(CompletableFuture<SessionManager> finalFuture,
                         Function<ConnectionManager<StompSession, WebSocketHttpHeaders, StompSessionHandler>, CompletableFuture<SessionManager>> connect,
                         Client client,
                         int attempt) {
        if (attempt > ValueUtil.get(client.getConnectionMaxRetries(), this.maxRetries, client.useGlobalValues())) {
            finalFuture.completeExceptionally(new TimeoutException("Connection failed attempt=%d".formatted(attempt)));
            return;
        }

        connect.apply(stompConnectionManager)
                .whenComplete((sessionManager, throwable) -> {
                    if (isNull(throwable) && nonNull(sessionManager) && sessionManager.isConnected()) {
                        finalFuture.complete(sessionManager);
                        log.info("Connected to server: client={} topic={}", client.getId(), client.getTopic());
                    } else {
                        log.info("Connection attempt failed: client={} topic={}", client.getId(), client.getTopic(), throwable);
                    }
                });

        if (finalFuture.isDone()) return;

        scheduledExecutor.schedule(() -> connect(finalFuture, connect, client, attempt + 1),
                ValueUtil.get(client.getConnectionTimeout(), this.timeout, client.useGlobalValues()),
                TimeUnit.MILLISECONDS);
    }
}
