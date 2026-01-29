package io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.facade.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.session.client.impl.SessionType;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionTimeoutManager;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientHelper;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientSession;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientState;
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
public class StompClientSessionTimeoutManager implements SessionTimeoutManager<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> {

    @Value("${ovymq.session-manager.connection.max-retries:10}")
    protected Integer maxRetries;

    @Value("${ovymq.session-manager.connection.timeout:1000}")
    protected Long timeout;

    private final ObjectProviderFacade objectProviderFacade;
    private final AbstractFactoryResolver abstractFactoryResolver;
    private final ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> stompClientState;
    private final ScheduledExecutorService scheduledExecutor;

    @Override
    public CompletableFuture<ClientHelper<StompSession>> execute(Client client,
                                                      ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientState,
                                                      ClientHelper<StompSession> clientHelper,
                                                      ClientSession<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientSession) {
        DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                .add(OvyMqConstants.DESTINATION, OvyMqConstants.DESTINATION_SERVER)
                .add(OvyMqConstants.SUBSCRIBED_TOPIC, client.getTopic())
                .add(OvyMqConstants.CLIENT_TYPE, client.getType().name())
                .add(OvyMqConstants.CLIENT_ID, client.getId());

        var headers = abstractFactoryResolver.create(definition, WebSocketHttpHeaders.class);

        if (headers.isPresent()) {
            Function<ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler>, CompletableFuture<ClientHelper<StompSession>>>
                    connect = stompClientState -> {
                if (!clientState.isConnected()) {
                    client.setLastHealthCheck(OffsetDateTime.now());
                    return stompClientState.connect("ws://localhost:9090/" + WS_REGISTRY, headers.get(), (StompClientSessionHandler) clientSession)
                            .thenApply(stompSession -> clientHelper);
                }
                return CompletableFuture.completedFuture(clientHelper);
            };

            connect(connect, client, clientHelper, clientState, 1);
            return clientHelper.getConnectionFuture();
        }
        return CompletableFuture.failedFuture(new IllegalStateException("Headers factory is not present"));
    }

    private void connect(Function<ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler>, CompletableFuture<ClientHelper<StompSession>>> connect,
                         Client client,
                         ClientHelper<StompSession> clientHelper,
                         ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientState,
                         int attempt) {
        if (attempt > ValueUtil.get(client.getConnectionMaxRetries(), this.maxRetries, client.useGlobalValues())) {
            clientHelper.getConnectionFuture().completeExceptionally(new TimeoutException("Connection failed attempt=%d".formatted(attempt)));
            return;
        }

        connect.apply(stompClientState)
                .whenComplete((returnedClientHelper, throwable) -> {
                    if (isNull(throwable) && nonNull(returnedClientHelper) && clientState.isConnected()) {
                        returnedClientHelper.getConnectionFuture().complete(returnedClientHelper);
                        log.info("Connected to server: client={} topic={}", client.getId(), client.getTopic());
                    } else {
                        log.info("Connection attempt failed: client={} topic={}", client.getId(), client.getTopic(), throwable);
                    }
                });

        if (clientHelper.getConnectionFuture().isDone()) return;

        scheduledExecutor.schedule(() -> connect(connect, client, clientHelper, clientState, attempt + 1),
                ValueUtil.get(client.getConnectionTimeout(), this.timeout, client.useGlobalValues()),
                TimeUnit.MILLISECONDS);
    }

    @Override
    public SessionType supports() {
        return SessionType.STOMP;
    }
}
