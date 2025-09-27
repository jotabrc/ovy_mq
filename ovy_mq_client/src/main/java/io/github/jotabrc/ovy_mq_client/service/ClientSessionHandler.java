package io.github.jotabrc.ovy_mq_client.service;

import lombok.Getter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

@Getter
public class ClientSessionHandler extends StompSessionHandlerAdapter {

    private final CompletableFuture<StompSession> future = new CompletableFuture<>();

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        super.handleFrame(headers, payload);
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        future.complete(session);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        future.completeExceptionally(exception);
    }
}
