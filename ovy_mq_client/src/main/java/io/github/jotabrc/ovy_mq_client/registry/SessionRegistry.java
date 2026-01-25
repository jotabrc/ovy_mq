package io.github.jotabrc.ovy_mq_client.registry;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
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

    private final Map<String, ClientAdapter> sessions = new ConcurrentHashMap<>();

    public void addOrReplace(String clientId, ClientAdapter session) {
        this.sessions.put(clientId, session);
    }

    public Optional<ClientAdapter> getById(String clientId) {
        return Optional.ofNullable(this.sessions.get(clientId));
    }

    @Deprecated(since = "0.7.0")
    public Optional<ClientAdapter> getByIdAndReconnectIfDisconnected(String clientId) {
        return Optional.ofNullable(this.sessions.get(clientId));
    }

    public Optional<ClientAdapter> removeById(String clientId) {
        return Optional.ofNullable(this.sessions.remove(clientId));
    }

    public Map<String, ClientAdapter> getAll() {
        return new HashMap<>(sessions);
    }
}
