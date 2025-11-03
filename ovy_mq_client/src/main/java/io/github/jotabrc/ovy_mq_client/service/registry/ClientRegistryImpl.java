package io.github.jotabrc.ovy_mq_client.service.registry;

import io.github.jotabrc.ovy_mq_client.handler.ClientNotFoundException;
import io.github.jotabrc.ovy_mq_client.service.ClientRegistryProvider;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientRegistryImpl implements ClientRegistry {

    private final ClientRegistryProvider clientRegistryProvider;
    private final ClientSessionRegistryProvider clientSessionRegistryProvider;

    @Override
    public void save(Client client) {
        if (nonNull(client) && nonNull(client.getTopic())) {
            clientRegistryProvider.getClients().compute(client.getTopic(), (key, queue) -> {
                if (isNull(queue)) queue = new ConcurrentLinkedQueue<>();
                if (!queue.contains(client)) queue.offer(client);
                return queue;
            });
            log.info("Client={} saved in registry for topic={}", client.getId(), client.getTopic());
        } else {
            log.error("Client cannot be saved in registry");
        }
    }

    @Override
    public Client getByClientIdOrThrow(String clientId) {
        return clientRegistryProvider.getClients().values()
                .stream()
                .flatMap(Collection::stream)
                .filter(client -> Objects.equals(clientId, client.getId()))
                .findFirst()
                .orElseThrow(() -> new ClientNotFoundException("Client %s not found".formatted(clientId)));
    }

    @Override
    public List<Client> getAllAvailableClients() {
        return clientRegistryProvider.getClients().values()
                .stream()
                .flatMap(Collection::stream)
                .filter(Client::getIsAvailable)
                .filter(client -> clientSessionRegistryProvider.getBy(client.getId())
                        .map(clientSessionHandler -> clientSessionHandler.getSession().isConnected())
                        .orElse(false))
                .toList();
    }

    @Override
    public List<Client> getAllClients() {
        return clientRegistryProvider.getClients().values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }
}
