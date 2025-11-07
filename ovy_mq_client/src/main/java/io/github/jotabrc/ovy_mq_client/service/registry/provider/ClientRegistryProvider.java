package io.github.jotabrc.ovy_mq_client.service.registry.provider;

import io.github.jotabrc.ovy_mq_client.handler.ClientNotFoundException;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.service.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientRegistryProvider {

    private final ClientRegistry clientRegistry;
    private final ClientSessionRegistryProvider clientSessionRegistryProvider;

    public void save(Client client) {
        if (nonNull(client) && nonNull(client.getTopic())) {
            clientRegistry.add(client);
            log.info("Client={} saved in registry for topic={}", client.getId(), client.getTopic());
        } else {
            log.error("Client cannot be saved in registry");
        }
    }

    public Client getByClientIdOrThrow(String clientId) {
        return clientRegistry.getById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Client %s not found".formatted(clientId)));
    }

    public List<Client> getAllAvailableClients() {
        return clientRegistry.getClients()
                .stream()
                .filter(Client::getIsAvailable)
                .filter(client -> clientSessionRegistryProvider.getById(client.getId())
                        .map(SessionManager::isConnected)
                        .orElse(false))
                .toList();
    }

    public List<Client> getAllClients() {
        return clientRegistry.getClients();
    }
}
