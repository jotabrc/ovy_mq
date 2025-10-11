package io.github.jotabrc.ovy_mq_client.service.domain.client;

import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.domain.factory.ObjectMapperFactory;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMessageHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Getter
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientSession extends StompSessionHandlerAdapter {

    private final ClientMessageHandler clientMessageHandler;
    private final CompletableFuture<StompSession> future = new CompletableFuture<>();

    private StompSession session;
    private String clientId;

    @Override
    public Type getPayloadType(StompHeaders headers) {
        String contentType = headers.getFirst("content-type");
        if ("text/plain".equalsIgnoreCase(contentType)) return String.class;
        return MessagePayload.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object object) {
        log.info("Handling frame {}", object);
        String destination = headers.getDestination();
        if (nonNull(destination) && destination.startsWith("/user/queue/")) {
            String topic = destination.substring("/user/queue/".length());
            MessagePayload messagePayload = ObjectMapperFactory.get().convertValue(object, MessagePayload.class);
            messagePayload.setTopic(topic);
            clientMessageHandler.handleMessage(clientId, topic, messagePayload);
        }
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        setSession(session);
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

    public void setClientId(String clientId) {
        if (isNull(this.clientId)) {
            this.clientId = clientId;
        }
    }
}
