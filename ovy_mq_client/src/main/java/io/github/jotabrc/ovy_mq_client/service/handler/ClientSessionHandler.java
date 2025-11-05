package io.github.jotabrc.ovy_mq_client.service.handler;

import io.github.jotabrc.ovy_mq_client.domain.factory.ObjectMapperFactory;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.service.handler.payload.PayloadDispatcher;
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

import static java.util.Objects.isNull;

@Slf4j
@Getter
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientSessionHandler extends StompSessionHandlerAdapter implements SessionManager {

    /*
    TODO:
    Interface SessionManager* for ClientSessionHandler
    Interface for WebSocketStompClient
    Interface for WebSocketHttpHeaders
    Interface for StompHeaders
     */
    private final CompletableFuture<SessionManager> future = new CompletableFuture<>();
    private final PayloadDispatcher payloadDispatcher;

    private StompSession session;
    private Client client;

    @Override
    public SessionManager send(StompHeaders headers, Object payload) {
        this.session.send(headers, payload);
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
        if (this.session.isConnected()) {
            this.session.disconnect();
            this.session = null;
        }
    }

    @Override
    public boolean isConnected() {
        return this.session.isConnected();
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
        payloadDispatcher.execute(client, object, headers);
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        /*
        TODO:
        subscribe here
        requires sending topic in headers on connect(..) call and returning from the server
         */
        future.complete(this);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        future.completeExceptionally(exception);
    }
}
