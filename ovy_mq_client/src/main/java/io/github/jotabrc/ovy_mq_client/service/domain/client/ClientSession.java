package io.github.jotabrc.ovy_mq_client.service.domain.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
@RequiredArgsConstructor
public class ClientSession extends StompSessionHandlerAdapter {

    private StompSession session;
    private final CompletableFuture<StompSession> future = new CompletableFuture<>();


    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        String destination = headers.getDestination();
        if (nonNull(destination) && destination.startsWith("/topic/")) {
            String topic = destination.substring(7);
            ClientExecutor.CLIENT_MESSAGE.getHandler().execute(topic, payload);
        }
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        future.complete(session);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        future.completeExceptionally(exception);
    }

    public void setSession(StompSession session) {
        if (isNull(this.session)) {
            this.session = session;
        }
    }
}
