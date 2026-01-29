package io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.stomp_handler;

import io.github.jotabrc.ovy_mq_client.session.client.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

@Slf4j
@RequiredArgsConstructor
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StompClientAdapter implements ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> {

    private final ClientSession<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientSession;
    private final ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientState;
    private final ClientHelper<StompSession> clientHelper;
    private final ClientInitializer<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientInitializer;
    private final ClientMessageSender<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientMessageSender;

    @Override
    public ClientSession<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> getClientSession() {
        return this.clientSession;
    }

    @Override
    public ClientState<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> getClientState() {
        return this.clientState;
    }

    @Override
    public ClientHelper<StompSession> getClientHelper() {
        return this.clientHelper;
    }

    @Override
    public ClientInitializer<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> getClientInitializer() {
        return this.clientInitializer;
    }

    @Override
    public ClientMessageSender<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> getClientMessageSender() {
        return this.clientMessageSender;
    }
}
