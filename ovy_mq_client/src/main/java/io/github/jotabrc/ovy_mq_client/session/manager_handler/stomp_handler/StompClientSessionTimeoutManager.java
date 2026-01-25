package io.github.jotabrc.ovy_mq_client.session.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.facade.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionTimeoutManager;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientHelper;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientState;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
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
public class StompClientSessionTimeoutManager implements SessionTimeoutManager<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> {

    @Value("${ovymq.session-manager.connection.max-retries:10}")
    protected Integer maxRetries;

    @Value("${ovymq.session-manager.connection.timeout:10000}")
    protected Long timeout;

    private final ObjectProviderFacade objectProviderFacade;
    private final AbstractFactoryResolver abstractFactoryResolver;
    private final ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> stompClientState;
    private final ScheduledExecutorService scheduledExecutor;

    @Override
    public CompletableFuture<ClientHelper<?>> execute(ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientAdapter) {
        DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                .add(OvyMqConstants.DESTINATION, OvyMqConstants.DESTINATION_SERVER)
                .add(OvyMqConstants.SUBSCRIBED_TOPIC, clientAdapter.getClientHelper().getClient().getTopic())
                .add(OvyMqConstants.CLIENT_TYPE, clientAdapter.getClientHelper().getClient().getType().name())
                .add(OvyMqConstants.CLIENT_ID, clientAdapter.getClientHelper().getClient().getId());

        var headers = abstractFactoryResolver.create(definition, WebSocketHttpHeaders.class);

        if (headers.isPresent()) {
            Function<ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler>, CompletableFuture<ClientHelper<?>>>
                    connect = stompClientState -> {
                if (!clientAdapter.getClientState().isConnected()) {
                    clientAdapter.getClientHelper().getClient().setLastHealthCheck(OffsetDateTime.now());
                    return stompClientState.connect("ws://localhost:9090/" + WS_REGISTRY, headers.get(), (StompClientSessionHandler) clientAdapter.getClientSession())
                            .thenApply(stompSession -> clientAdapter.getClientHelper());
                }
                return CompletableFuture.completedFuture(clientAdapter.getClientHelper());
            };

            connect(connect, clientAdapter, 1);
            return clientAdapter.getClientHelper().getConnectionFuture();
        }
        return CompletableFuture.failedFuture(new IllegalStateException("Headers factory is not present"));
    }

    private void connect(Function<ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler>, CompletableFuture<ClientHelper<?>>> connect,
                         ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientAdapter,
                         int attempt) {
        if (attempt > ValueUtil.get(clientAdapter.getClientHelper().getClient().getConnectionMaxRetries(), this.maxRetries, clientAdapter.getClientHelper().getClient().useGlobalValues())) {
            clientAdapter.getClientHelper().getConnectionFuture().completeExceptionally(new TimeoutException("Connection failed attempt=%d".formatted(attempt)));
            return;
        }

        connect.apply(stompClientState)
                .whenComplete((clientHelper, throwable) -> {
                    if (isNull(throwable) && nonNull(clientHelper) && clientAdapter.getClientState().isConnected()) {
                        clientAdapter.getClientHelper().getConnectionFuture().complete(clientHelper);
                        log.info("Connected to server: client={} topic={}", clientAdapter.getClientHelper().getClient().getId(), clientAdapter.getClientHelper().getClient().getTopic());
                    } else {
                        log.info("Connection attempt failed: client={} topic={}", clientAdapter.getClientHelper().getClient().getId(), clientAdapter.getClientHelper().getClient().getTopic(), throwable);
                    }
                });

        if (clientAdapter.getClientHelper().getConnectionFuture().isDone()) return;

        scheduledExecutor.schedule(() -> connect(connect, clientAdapter, attempt + 1),
                ValueUtil.get(clientAdapter.getClientHelper().getClient().getConnectionTimeout(), this.timeout, clientAdapter.getClientHelper().getClient().useGlobalValues()),
                TimeUnit.MILLISECONDS);
    }

    @Override
    public SessionType supports() {
        return SessionType.STOMP;
    }
}
