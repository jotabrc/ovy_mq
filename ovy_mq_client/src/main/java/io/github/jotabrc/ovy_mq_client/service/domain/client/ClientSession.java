package io.github.jotabrc.ovy_mq_client.service.domain.client;

import io.github.jotabrc.ovy_mq_client.domain.ActionFactory;
import io.github.jotabrc.ovy_mq_client.domain.ClientFactory;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

import static io.github.jotabrc.ovy_mq_client.domain.Command.PROCESS_RECEIVED_MESSAGE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Getter
@RequiredArgsConstructor
public class ClientSession extends StompSessionHandlerAdapter {

    private StompSession session;
    private final CompletableFuture<StompSession> future = new CompletableFuture<>();


    @Override
    public Type getPayloadType(StompHeaders headers) {
        String contentType = headers.getFirst("content-type");
        if ("text/plain".equalsIgnoreCase(contentType)) return String.class;
        return MessagePayload.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        log.info("Handling frame {}", payload);
        String destination = headers.getDestination();
        if (nonNull(destination) && destination.startsWith("/user/queue/")) {
            String topic = destination.substring("/user/queue/".length());
            ActionFactory.of(ClientFactory.createConsumer(topic, this), (MessagePayload) payload).execute(PROCESS_RECEIVED_MESSAGE);
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
