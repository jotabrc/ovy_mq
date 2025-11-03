package io.github.jotabrc.ovy_mq_client.service.registry;

import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
public class ClientRegistryProvider {

    private final Map<String, Queue<Client>> clients = new ConcurrentHashMap<>();

    public Optional<Client> getById(String clientId) {
        return clients.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(client -> Objects.equals(clientId, client.getId()))
                .findFirst();
    }

    public List<Client> getClients() {
        return clients.values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    public void add(Client client) {
        clients.compute(client.getTopic(), (key, queue) -> {
            if (isNull(queue)) queue = new ConcurrentLinkedQueue<>();
            if (!queue.contains(client)) queue.offer(client);
            return queue;
        });
    }

    public void removeById(String clientId) {
        clients.computeIfPresent(clientId, (key, queue) -> {
            queue.removeIf(client -> Objects.equals(clientId, client.getId()));
            return queue;
        });
    }
}

