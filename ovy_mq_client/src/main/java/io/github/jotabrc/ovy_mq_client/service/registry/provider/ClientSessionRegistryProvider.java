package io.github.jotabrc.ovy_mq_client.service.registry.provider;

import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.service.registry.ClientSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientSessionRegistryProvider {

    private final ClientSessionRegistry clientSessionRegistry;

    public void addOrReplace(String clientId, SessionManager session) {
        clientSessionRegistry.addOrReplace(clientId, session);
    }

    public Optional<SessionManager> getById(String clientId) {
        return clientSessionRegistry.getById(clientId);
    }

    public void removeById(String clientId) {
        clientSessionRegistry.removeById(clientId);
    }
    /*
    TODO:
    - wrapper/adapter for session
    - return session wrapper/adapter
     */
}
