package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_client.service.components.factory.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_client.service.components.factory.domain.StompHeadersDto;
import io.github.jotabrc.ovy_mq_client.service.components.factory.domain.WebSocketHttpHeadersDto;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.HealthStatus;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import io.github.jotabrc.ovy_mq_core.domain.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.CONFIRM_PAYLOAD_RECEIVED;
import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.WS_REGISTRY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompSessionHandler extends SessionManager {

    protected final PayloadHandlerDispatcher payloadHandlerDispatcher;
    protected final AbstractFactoryResolver abstractFactoryResolver;
    protected final PayloadConfirmationHandlerDispatcher payloadConfirmationHandlerDispatcher;
    protected final WebSocketStompClient webSocketStompClient;
    private final Executor smallPoolExecutor;

    protected CompletableFuture<SessionManager> future;
    protected StompSession session;
    protected Client client;
    protected List<String> subscriptions;

    @org.springframework.beans.factory.annotation.Value("${ovymq.session.connection.timeout}")
    protected Long connectionTimeout;

    @org.springframework.beans.factory.annotation.Value("${ovymq.session.connection.backoff}")
    protected Integer connectionBackoff;

    @Override
    public SessionManager send(String destination, Object payload) {
        synchronized (this) {
            StompHeadersDto dto = new StompHeadersDto(destination, client.getTopic(), client.getType().name());
            abstractFactoryResolver.create(dto, dto.getReturns())
                    .ifPresent(headers -> {
                        try {
                            this.reconnectIfNotAlive(false);
                            this.session.send(headers, payload);
                        } catch (NullPointerException e) {
                            log.error("Session is null cannot send message");
                            initialize();
                        }
                    });
            return this;
        }
    }

    @Override
    public void initialize() {
        synchronized (this) {
            smallPoolExecutor.execute(() -> {
                log.info("Initializing-session client={}", client.getId());
                WebSocketHttpHeadersDto dto = new WebSocketHttpHeadersDto("server", client.getTopic(), client.getType().name());
                abstractFactoryResolver.create(dto, dto.getReturns())
                        .ifPresent(headers -> {
                            Runnable connect = () -> {
                                this.connect("ws://localhost:9090/" + WS_REGISTRY, headers);
                                this.client.setLastHealthCheck(OffsetDateTime.now());
                            };

                            AtomicInteger counter = new AtomicInteger(0);
                            while (counter.getAndIncrement() < connectionBackoff) {
                                try {
                                    this.future = new CompletableFuture<>();
                                    connect.run();
                                    this.future.get(connectionTimeout, TimeUnit.MILLISECONDS);
                                } catch (Exception e) {
                                    log.error("Error while connecting to server client={}", client.getId(), e);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ex) {
                                        Thread.currentThread().interrupt();
                                    }
                                    log.info("Server is unavailable, retrying connection. Retry-number={} client={} topic={}", counter.get(), this.client.getId(), this.client.getTopic());
                                }

                                if (Objects.equals(counter.get(), connectionBackoff) && isNull(this.session))
                                    log.warn("Client={} Failed to connect to server after {} tries", client.getId(), counter.get());
                            }
                        });

                if (this.isConnected())
                    log.info("Session initialized: client={} topic={}", client.getId(), client.getTopic());
            });
        }
    }

    protected void connect(String url, WebSocketHttpHeaders headers) {
        webSocketStompClient.connectAsync(url, headers, this);
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
        future.complete(this);
    }

    protected void subscribe() {
        subscriptions.forEach(destination -> this.session.subscribe(destination, this));
    }

    @Override
    public void handleTransportError(@NotNull StompSession session, @NotNull Throwable exception) {
        future.completeExceptionally(exception);
    }
}
