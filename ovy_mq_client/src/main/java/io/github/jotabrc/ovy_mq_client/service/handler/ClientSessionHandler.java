package io.github.jotabrc.ovy_mq_client.service.handler;

import io.github.jotabrc.ovy_mq_client.domain.HealthStatus;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.service.handler.executor.ClientMessageHandlerExecutor;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientMessageHandler;
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

@Slf4j
@Getter
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientSessionHandler extends StompSessionHandlerAdapter {

    private final ClientMessageHandlerExecutor clientMessageHandlerExecutor;
    private final ClientMessageHandler clientMessageHandler;
    private final CompletableFuture<StompSession> future = new CompletableFuture<>();

    private StompSession session;
    private String clientId;

    @Override
    public Type getPayloadType(StompHeaders headers) {
        String customContentType = headers.getFirst("payload-type");
        if ("message-payload".equalsIgnoreCase(customContentType)) return MessagePayload.class;
        if ("health-status".equalsIgnoreCase(customContentType)) return HealthStatus.class;

        return Void.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object object) {
        clientMessageHandlerExecutor.execute(headers, object,this.clientId, this.getPayloadType(headers));
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

    public void setClientId(String clientId) {
        if (isNull(this.clientId)) {
            this.clientId = clientId;
        }
    }
}
