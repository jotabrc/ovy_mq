package io.github.jotabrc.ovy_mq_client.service.registry;

import io.github.jotabrc.ovy_mq_client.handler.ClientNotFoundException;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientRegistry {

    private final ClientRegistryProvider clientRegistryProvider;
    private final ClientSessionRegistryProvider clientSessionRegistryProvider;

    public void save(Client client) {
        if (nonNull(client) && nonNull(client.getTopic())) {
            clientRegistryProvider.add(client);
            log.info("Client={} saved in registry for topic={}", client.getId(), client.getTopic());
        } else {
            log.error("Client cannot be saved in registry");
        }
    }

    public Client getByClientIdOrThrow(String clientId) {
        return clientRegistryProvider.getById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Client %s not found".formatted(clientId)));
    }

    public List<Client> getAllAvailableClients() {
        return clientRegistryProvider.getClients()
                .stream()
                .filter(Client::getIsAvailable)
                .filter(client -> clientSessionRegistryProvider.getById(client.getId())
                        .map(StompSession::isConnected)
                        .orElse(false))
                .toList();
    }

    public List<Client> getAllClients() {
        return clientRegistryProvider.getClients();
    }
}
