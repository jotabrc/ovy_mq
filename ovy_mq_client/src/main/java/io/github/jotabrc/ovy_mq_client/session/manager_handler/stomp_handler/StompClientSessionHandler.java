package io.github.jotabrc.ovy_mq_client.session.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.facade.DispatcherFacade;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientHelper;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientSession;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.ListenerConfig;
import io.github.jotabrc.ovy_mq_core.domain.payload.HealthStatus;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.lang.reflect.Type;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompClientSessionHandler extends StompSessionHandlerAdapter implements ClientSession<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> {

    private final DispatcherFacade dispatcherFacade;
    private ClientHelper<StompSession> clientHelper;

    @Override
    public Type getPayloadType(StompHeaders headers) {
        String contentType = headers.getFirst(OvyMqConstants.PAYLOAD_TYPE);
        if (OvyMqConstants.PAYLOAD_TYPE_MESSAGE_PAYLOAD.equalsIgnoreCase(contentType)) return MessagePayload.class;
        if (OvyMqConstants.PAYLOAD_TYPE_HEALTH_STATUS.equalsIgnoreCase(contentType)) return HealthStatus.class;
        if (OvyMqConstants.PAYLOAD_TYPE_LISTENER_CONFIG.equalsIgnoreCase(contentType)) return ListenerConfig.class;

        return Void.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object object) {
        dispatcherFacade.acknowledgePayload(this.clientHelper.getClient(), object);
        dispatcherFacade.handlePayload(this.clientHelper.getClient(), object, headers);
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.clientHelper.setSession(session);

        this.subscribe();
        if (nonNull(this.clientHelper.getConnectionFuture())
                && !this.clientHelper.getConnectionFuture().isDone()) {
            this.clientHelper.getConnectionFuture().complete(clientHelper);
        }
    }

    private void subscribe() {
        this.clientHelper.getSubscriptions()
                .forEach(destination -> this.clientHelper.getSession().subscribe(destination, this));
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.error("Error: ", exception);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.error("Error while handling payload: {}", exception.getMessage(), exception);
    }

    @Override
    public void setClientHelper(ClientHelper<StompSession> clientHelper) {
        if (isNull(this.clientHelper) && nonNull(clientHelper))
            this.clientHelper = clientHelper;
    }
}
