package io.github.jotabrc.ovy_mq_client.service.registry;

import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientSessionRegistry {

    private final Map<String, SessionManager> sessions = new ConcurrentHashMap<>();

    public void addOrReplace(String clientId, SessionManager session) {
        this.sessions.put(clientId, session);
    }

    public Optional<SessionManager> getById(String clientId) {
        return Optional.of(this.sessions.get(clientId));
    }

    public void removeById(String clientId) {
        this.sessions.remove(clientId);
    }
}
