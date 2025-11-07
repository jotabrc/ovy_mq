package io.github.jotabrc.ovy_mq_client.service.handler.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.Client;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.concurrent.CompletableFuture;

public interface SessionManager {

    SessionManager send(String destination, Object payload);
    CompletableFuture<SessionManager> connect(String url, WebSocketHttpHeaders headers);
    SessionManager subscribe(String destination);
    void setClient(Client client);
    Client getClient();
    void disconnect();
    boolean isConnected();
}
