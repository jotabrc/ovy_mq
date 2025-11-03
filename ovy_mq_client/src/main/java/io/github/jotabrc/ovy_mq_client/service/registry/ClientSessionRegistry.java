package io.github.jotabrc.ovy_mq_client.service.registry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientSessionRegistry {

    private final Map<String, StompSession> sessions = new ConcurrentHashMap<>();

    public void addOrReplace(String clientId, StompSession session) {
        this.sessions.put(clientId, session);
    }

    public Optional<StompSession> getById(String clientId) {
        return Optional.of(this.sessions.get(clientId));
    }

    public void removeById(String clientId) {
        this.sessions.remove(clientId);
    }
}
