package io.github.jotabrc.ovy_mq.service;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void register(String id, String token, WebSocketSession session) {
        sessions.put(id, session);
    }

    public void unregister(String clientId) {
        sessions.remove(clientId);
    }

    public void sendTo(String clientId, String message) {
        WebSocketSession session = sessions.get(clientId);
        if (session != null && session.isOpen()) {
            session.send(Mono.just(session.textMessage(message))).subscribe();
        }
    }
}
