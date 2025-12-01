package io.github.jotabrc.ovy_mq_client.component.session.stomp;

import io.github.jotabrc.ovy_mq_client.component.DispatcherFacade;
import io.github.jotabrc.ovy_mq_client.component.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.component.session.SessionTimeoutManagerResolver;
import io.github.jotabrc.ovy_mq_client.component.session.SessionType;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.component.session.stomp.manager.ManagerFactory;
import io.github.jotabrc.ovy_mq_client.component.session.stomp.manager.ManagerHandler;
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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.CONFIRM_PAYLOAD_RECEIVED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompSessionHandler extends StompSessionHandlerAdapter implements SessionManager {

    private final ManagerHandler managerHandler;
    private final DispatcherFacade dispatcherFacade;
    private final ObjectProviderFacade objectProviderFacade;
    private final AbstractFactoryResolver abstractFactoryResolver;
    private final SessionTimeoutManagerResolver sessionTimeoutManagerResolver;

    private StompSession session;
    private Client client;
    private List<String> subscriptions;
    private CompletableFuture<SessionManager> connectionFuture;
    private List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();

    @Override
    public SessionManager send(String destination, Object payload) {
        synchronized (this) {
            DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                    .add(Key.HEADER_DESTINATION, destination)
                    .add(Key.HEADER_TOPIC, client.getTopic())
                    .add(Key.HEADER_CLIENT_TYPE, client.getType().name())
                    .add(Key.HEADER_CLIENT_ID, client.getId());
            abstractFactoryResolver.create(definition, StompHeaders.class)
                    .ifPresent(headers -> {
                        if (this.isConnected()) {
                            this.session.send(headers, payload);
                        } else {
                            log.error("Failed to send message: client={} client-type={} sessionManager-connected={}", client.getId(), client.getType(), this.isConnected());
                        }
                    });
            return this;
        }
    }

    @Override
    public void initializeHandler() {
        if (nonNull(this.client)) {
            scheduledFutures.addAll(managerHandler.initialize(client, this,
                    ManagerFactory.HEALTH_CHECK,
                    ManagerFactory.LISTENER_POLL));
        } else throw new IllegalStateException("SessionManager initialized without a Client");
    }

    @Override
    public void initializeSession() {
        log.info("Initializing-sessionManager client={}", client.getId());
        connectionFuture = new CompletableFuture<>();
        sessionTimeoutManagerResolver.get(SessionType.STOMP)
                .ifPresent(sessionTimeoutManager -> sessionTimeoutManager.execute(this, client, connectionFuture)
                        .whenComplete(((sessionManager, throwable) -> {
                            if (nonNull(sessionManager) && sessionManager.isConnected() && isNull(throwable))
                                log.info("Session initialized: client={} topic={}", client.getId(), client.getTopic());
                            else
                                log.info("Session failed to initialize: client={} topic={}", client.getId(), client.getTopic());
                        })));
    }

    @Override
    public boolean isConnected() {
        return nonNull(session) && this.session.isConnected();
    }

    @Override
    public boolean canDisconnect() {
        return isConnected() && this.client.canDisconnect();
    }

    @Override
    public void disconnect() {
        if (nonNull(this.session) && this.isConnected() && this.canDisconnect()) this.session.disconnect();
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

    @Override
    public void destroy() {
        if (!scheduledFutures.isEmpty()) {
            log.info("Destroying session for client: {}. Cancelling {} scheduled tasks.", client.getId(), scheduledFutures.size());
            this.client.setIsDestroying(true);
            scheduledFutures.forEach(scheduledFuture -> {
                if (!scheduledFuture.isDone() && !scheduledFuture.isCancelled()) scheduledFuture.cancel(true);
            });
            scheduledFutures.clear();
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
        dispatcherFacade.acknowledgePayload(this, client, CONFIRM_PAYLOAD_RECEIVED, object);
        dispatcherFacade.handlePayload(client, object, headers);
    }

    @Override
    public void afterConnected(@NotNull StompSession session, @NotNull StompHeaders connectedHeaders) {
        this.session = session;
        this.subscribe();
        if (nonNull(this.connectionFuture) && !this.connectionFuture.isDone()) {
            this.connectionFuture.complete(this);
        }
    }

    private void subscribe() {
        subscriptions.forEach(destination -> this.session.subscribe(destination, this));
    }

    @Override
    public void handleTransportError(@NotNull StompSession session, @NotNull Throwable exception) {
        log.error("Error: ", exception);
    }
}
