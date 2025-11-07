package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_client.domain.factory.ObjectMapperFactory;
import io.github.jotabrc.ovy_mq_client.service.components.HeadersFactoryResolver;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.HealthStatus;
import io.github.jotabrc.ovy_mq_core.domain.MessagePayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Getter
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompSessionHandler extends StompSessionHandlerAdapter implements SessionManager {

    /*
    TODO:
    Interface for WebSocketStompClient
    Interface for WebSocketHttpHeaders
     */
    private final CompletableFuture<SessionManager> future = new CompletableFuture<>();
    private final PayloadHandlerDispatcher payloadHandlerDispatcher;
    private final HeadersFactoryResolver headersFactoryResolver;
    private final PayloadConfirmationHandlerDispatcher payloadConfirmationHandlerDispatcher;

    private StompSession session;
    private Client client;

    @Override
    public SessionManager send(String destination, Object payload) {
        headersFactoryResolver.getFactory(StompHeaders.class)
                .ifPresentOrElse(ovyHeaders -> {
                            StompHeaders headers = (StompHeaders) ovyHeaders.createDefault(destination, client.getTopic());
                            this.session.send(headers, payload);
                        },
                        () -> log.warn("No factory available for class-type={}", payload.getClass()));
        return this;
    }

    @Override
    public CompletableFuture<SessionManager> connect(String url, WebSocketHttpHeaders headers) {
        ObjectMapperFactory.getWithConverter().connectAsync(url, headers, this);
        return future;
    }

    @Override
    public SessionManager subscribe(String destination) {
        this.session.subscribe(destination, this);
        return this;
    }

    @Override
    public void setClient(Client client) {
        if (isNull(this.client)) {
            this.client = client;
        }
    }

    @Override
    public void disconnect() {
        if (nonNull(this.session) && this.session.isConnected()) {
            this.session.disconnect();
            this.session = null;
        }
    }

    @Override
    public boolean isConnected() {
        return this.session.isConnected();
    }

    @Override
    public void changeClientAvailabilityTo(boolean isAvailable) {
        client.setIsAvailable(isAvailable);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        String customContentType = headers.getFirst(Key.HEADER_PAYLOAD_TYPE);
        if (Key.PAYLOAD_TYPE_MESSAGE_PAYLOAD.equalsIgnoreCase(customContentType)) return MessagePayload.class;
        if (Key.PAYLOAD_TYPE_HEALTH_STATUS.equalsIgnoreCase(customContentType)) return HealthStatus.class;

        return Void.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object object) {
        payloadConfirmationHandlerDispatcher.execute(this, client, client.confirmPayloadReceived(), object);
        payloadHandlerDispatcher.execute(client, object, headers);
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        this.subscribe(WS_USER + WS_HEALTH)
                .subscribe(WS_USER + WS_QUEUE + "/" + client.getTopic());
        future.complete(this);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        future.completeExceptionally(exception);
    }
}
