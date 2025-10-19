package io.github.jotabrc.ovy_mq_client.service.registry;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.handler.ClientNotFoundException;
import io.github.jotabrc.ovy_mq_client.service.ClientRegistryContextHolder;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
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

    private final ClientRegistryContextHolder clientRegistryContextHolder;

    @Override
    public void save(Client client) {
        if (nonNull(client)
                && nonNull(client.getTopic())
                && nonNull(client.getClientSessionHandler())) {
            clientRegistryContextHolder.getClients().compute(client.getTopic(), (key, queue) -> {
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
        return clientRegistryContextHolder.getClients().values()
                .stream()
                .flatMap(Collection::stream)
                .filter(client -> Objects.equals(clientId, client.getId()))
                .findFirst()
                .orElseThrow(() -> new ClientNotFoundException("Client %s not found".formatted(clientId)));
    }

    @Override
    public List<Client> getAllAvailableClients() {
        return clientRegistryContextHolder.getClients().values()
                .stream()
                .flatMap(Collection::stream)
                .filter(Client::getIsAvailable)
                .filter(client -> client.getClientSessionHandler().getSession().isConnected())
                .toList();
    }

    @Override
    public List<Client> getAllClients() {
        return clientRegistryContextHolder.getClients().values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }
}
