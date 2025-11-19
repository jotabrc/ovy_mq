package io.github.jotabrc.ovy_mq_client.component.session;

import io.github.jotabrc.ovy_mq_client.component.payload.PayloadConfirmationHandlerDispatcher;
import io.github.jotabrc.ovy_mq_client.component.payload.PayloadHandlerDispatcher;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.HealthStatus;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import io.github.jotabrc.ovy_mq_core.domain.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.CONFIRM_PAYLOAD_RECEIVED;
import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.WS_REGISTRY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompSessionHandler extends StompSessionHandlerAdapter implements SessionManager {

    private final PayloadHandlerDispatcher payloadHandlerDispatcher;
    private final AbstractFactoryResolver abstractFactoryResolver;
    private final PayloadConfirmationHandlerDispatcher payloadConfirmationHandlerDispatcher;
    private final WebSocketStompClient webSocketStompClient;
    private final SessionTimeoutManager sessionTimeoutManager;
    private final ObjectProvider<DefinitionMap> definitionProvider;

    private CompletableFuture<SessionManager> future;
    private StompSession session;
    private Client client;
    private List<String> subscriptions;

    @Override
    public SessionManager send(String destination, Object payload) {
        synchronized (this) {
            DefinitionMap definition = definitionProvider.getObject()
                    .add(Key.HEADER_DESTINATION, destination)
                    .add(Key.HEADER_TOPIC, client.getTopic())
                    .add(Key.HEADER_CLIENT_TYPE, client.getType().name())
                    .add(Key.HEADER_CLIENT_ID, client.getId());
            abstractFactoryResolver.create(definition, StompHeaders.class)
                    .ifPresent(headers -> {
                        try {
                            this.reconnectIfNotAlive(false);
                            this.session.send(headers, payload);
                        } catch (NullPointerException e) {
                            log.error("Failed to send message: client={} client-type={}", client.getId(), client.getType(), e);
                            initialize();
                        }
                    });
            return this;
        }
    }

    @Override
    public void initialize() {
        log.info("Initializing-session client={}", client.getId());
        DefinitionMap definition = definitionProvider.getObject()
                .add(Key.HEADER_DESTINATION, Value.DESTINATION_SERVER)
                .add(Key.HEADER_TOPIC, client.getTopic())
                .add(Key.HEADER_CLIENT_TYPE, client.getType().name())
                .add(Key.HEADER_CLIENT_ID, client.getId());
        abstractFactoryResolver.create(definition, WebSocketHttpHeaders.class)
                .ifPresent(headers -> {
                    Runnable connect = () -> {
                        this.client.setLastHealthCheck(OffsetDateTime.now());
                        this.connect("ws://localhost:9090/" + WS_REGISTRY, headers);
                    };
                    Callable<Boolean> isConnected = this::isConnected;
                    future = sessionTimeoutManager.manage(this.future, connect, isConnected, client);
                });

        if (this.isConnected())
            log.info("Session initialized: client={} topic={}", client.getId(), client.getTopic());
    }

    protected CompletableFuture<StompSession> connect(String url, WebSocketHttpHeaders headers) {
        return webSocketStompClient.connectAsync(url, headers, this);
    }

    @Override
    public SessionManager reconnectIfNotAlive(boolean force) {
        log.info("Session connection status: alive={}", nonNull(session) && session.isConnected());
        if (isNull(session) || !session.isConnected() || force) {
            if (nonNull(session) && session.isConnected()) session.disconnect();
            session = null;
            this.initialize();
        }
        return this;
    }

    @Override
    public boolean isConnected() {
        return nonNull(session) && this.session.isConnected();
    }

    @Override
    public void setClient(Client client) {
        if (isNull(this.client)) {
            this.client = client;
        }
    }

    @Override
    public void setSubscriptions(List<String> subscriptions) {
        if (isNull(this.subscriptions) || this.subscriptions.isEmpty()) {
            this.subscriptions = subscriptions;
        }
    }

    @NotNull
    @Override
    public Type getPayloadType(@NotNull StompHeaders headers) {
        String contentType = headers.getFirst(Key.HEADER_PAYLOAD_TYPE);
        if (Value.PAYLOAD_TYPE_MESSAGE_PAYLOAD.equalsIgnoreCase(contentType)) return MessagePayload.class;
        if (Value.PAYLOAD_TYPE_HEALTH_STATUS.equalsIgnoreCase(contentType)) return HealthStatus.class;
        if (Value.PAYLOAD_TYPE_LISTENER_CONFIG.equalsIgnoreCase(contentType)) return ListenerConfig.class;

        return Void.class;
    }

    @Override
    public void handleFrame(@NotNull StompHeaders headers, Object object) {
        payloadConfirmationHandlerDispatcher.execute(this, client, CONFIRM_PAYLOAD_RECEIVED, object);
        payloadHandlerDispatcher.execute(client, object, headers);
    }

    @Override
    public void afterConnected(@NotNull StompSession session, @NotNull StompHeaders connectedHeaders) {
        this.session = session;
        this.subscribe();
        this.future.complete(this);
    }

    protected void subscribe() {
        subscriptions.forEach(destination -> this.session.subscribe(destination, this));
    }

    @Override
    public void handleTransportError(@NotNull StompSession session, @NotNull Throwable exception) {
        log.error("Error: ", exception);
    }
}
