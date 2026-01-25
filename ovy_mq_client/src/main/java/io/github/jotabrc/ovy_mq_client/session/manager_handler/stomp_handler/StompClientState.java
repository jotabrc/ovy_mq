package io.github.jotabrc.ovy_mq_client.session.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientState;
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
    private ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientAdapter;

    @Override
    public CompletableFuture<StompSession> connect(String url, WebSocketHttpHeaders headers, StompClientSessionHandler sessionHandler) {
        return webSocketStompClient.connectAsync(url, headers, sessionHandler);
    }

    @Override
    public boolean isConnected() {
        return nonNull(this.clientAdapter.getClientHelper().getSession()) && this.clientAdapter.getClientHelper().getSession().isConnected();
    }

    @Override
    public boolean disconnect(boolean force) {
        if ((nonNull(this.clientAdapter.getClientHelper().getSession()) && this.isConnected() && this.clientAdapter.getClientHelper().getClient().canDisconnect())
                || force) {
            this.clientAdapter.getClientHelper().getSession().disconnect();
            if (this.clientAdapter.getClientHelper().getClient().getIsDestroying()) this.stop();
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
        if (!this.clientAdapter.getClientHelper().getScheduledFutures().isEmpty()) {
            log.info("Destroying stompSession for client: {}. Cancelling {} scheduled tasks.", this.clientAdapter.getClientHelper().getClient().getId(), this.clientAdapter.getClientHelper().getScheduledFutures().size());
            this.clientAdapter.getClientHelper().getClient().setIsDestroying(true);
            this.clientAdapter.getClientHelper().getScheduledFutures().forEach(scheduledFuture -> {
                if (!scheduledFuture.isDone() && !scheduledFuture.isCancelled()) scheduledFuture.cancel(true);
            });
            this.clientAdapter.getClientHelper().getScheduledFutures().clear();
        }
        return this.disconnect(force);
    }

    @Override
    public void setClientAdapter(ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientAdapter) {
        if (isNull(this.clientAdapter)) this.clientAdapter = clientAdapter;
    }
}
