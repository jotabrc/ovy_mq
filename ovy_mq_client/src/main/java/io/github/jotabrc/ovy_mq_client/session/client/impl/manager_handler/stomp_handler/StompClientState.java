package io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientHelper;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompClientState implements ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> {

    private final WebSocketStompClient webSocketStompClient;
    private ClientHelper<StompSession> clientHelper;

    @Override
    public CompletableFuture<StompSession> connect(String url, WebSocketHttpHeaders headers, StompClientSessionHandler sessionHandler) {
        return webSocketStompClient.connectAsync(url, headers, sessionHandler);
    }

    @Override
    public boolean isConnected() {
        return nonNull(this.clientHelper.getSession()) && this.clientHelper.getSession().isConnected();
    }

    @Override
    public boolean disconnect(boolean force) {
        if ((nonNull(this.clientHelper.getSession()) && this.isConnected() && this.clientHelper.getClient().canDisconnect())
                || force) {
            this.clientHelper.getSession().disconnect();
            if (this.clientHelper.getClient().getState().getDestroying().get()) this.stop();
            return true;
        }
        return false;
    }

    @Override
    public void stop() {
        webSocketStompClient.stop();
    }

    @Override
    public boolean destroy(boolean force) {
        if (!this.clientHelper.getScheduledFutures().isEmpty()) {
            log.info("Destroying stompSession for client: {}. Cancelling {} scheduled tasks.", this.clientHelper.getClient().getId(), this.clientHelper.getScheduledFutures().size());
            this.clientHelper.getClient().setDestroying(true);
            this.clientHelper.getScheduledFutures().forEach(scheduledFuture -> {
                if (!scheduledFuture.isDone() && !scheduledFuture.isCancelled()) scheduledFuture.cancel(true);
            });
            this.clientHelper.getScheduledFutures().clear();
        }
        return this.disconnect(force);
    }

    @Override
    public void setClientHelper(ClientHelper<StompSession> clientHelper) {
        if (isNull(this.clientHelper) && nonNull(clientHelper))
            this.clientHelper = clientHelper;
    }
}
