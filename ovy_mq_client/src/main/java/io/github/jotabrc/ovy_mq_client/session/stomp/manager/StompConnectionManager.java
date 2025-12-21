package io.github.jotabrc.ovy_mq_client.session.stomp.manager;

import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_client.session.stomp.StompSessionHandler;
import io.github.jotabrc.ovy_mq_client.session.interfaces.ConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class StompConnectionManager implements ConnectionManager<StompSession, WebSocketHttpHeaders, StompSessionHandler> {

    private final WebSocketStompClient webSocketStompClient;

    @Override
    public CompletableFuture<StompSession> connect(String url, WebSocketHttpHeaders headers, StompSessionHandler sessionHandler) {
        return webSocketStompClient.connectAsync(url, headers, sessionHandler);
    }

    @Override
    public void stop() {
        webSocketStompClient.stop();
    }

    @Override
    public SessionType supports() {
        return SessionType.STOMP;
    }
}
