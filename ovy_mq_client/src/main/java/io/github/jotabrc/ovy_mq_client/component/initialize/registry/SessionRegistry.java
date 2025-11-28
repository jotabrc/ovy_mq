package io.github.jotabrc.ovy_mq_client.component.initialize.registry;

import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class SessionRegistry {

    private final Map<String, SessionManager> sessions = new ConcurrentHashMap<>();

    public void addOrReplace(String clientId, SessionManager session) {
        this.sessions.put(clientId, session);
    }

    public Optional<SessionManager> getById(String clientId) {
        return Optional.ofNullable(this.sessions.get(clientId));
    }

    public Optional<SessionManager> getByIdAndReconnectIfDisconnected(String clientId) {
        return Optional.ofNullable(this.sessions.get(clientId));
    }

    public Optional<SessionManager> removeById(String clientId) {
        return Optional.ofNullable(this.sessions.remove(clientId));
    }

    public Map<String, SessionManager> getAll() {
        return new HashMap<>(sessions);
    }
}
