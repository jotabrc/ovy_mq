package io.github.jotabrc.ovy_mq_client.service.domain.client;

import io.github.jotabrc.ovy_mq_client.domain.*;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.ClientHandler;
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
            Client client = ClientFactory.createConsumer(topic, this);
            Action action = ActionFactory.create(client, (MessagePayload) payload, Command.EXECUTE_CLIENT_MESSAGE_HANDLER_HANDLE_MESSAGE);
            ClientHandler.CLIENT_MESSAGE.getHandler().execute(action);
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
