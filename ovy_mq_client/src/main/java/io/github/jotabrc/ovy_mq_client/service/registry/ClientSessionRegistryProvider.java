package io.github.jotabrc.ovy_mq_client.service.registry;

import io.github.jotabrc.ovy_mq_client.service.handler.ClientSessionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientSessionRegistryProvider {

    private Map<String, ClientSessionHandler> sessions = new ConcurrentHashMap<>();

    public void addOrReplace(String clientId, ClientSessionHandler clientSessionHandler) {
        this.sessions.put(clientId, clientSessionHandler);
    }

    public Optional<ClientSessionHandler> getBy(String clientId) {
        return Optional.of(this.sessions.get(clientId));
    }
}
