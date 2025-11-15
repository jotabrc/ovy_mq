package io.github.jotabrc.ovy_mq_client.service.registry;

import io.github.jotabrc.ovy_mq_client.handler.ClientNotFoundException;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientRegistry {

    private final SessionRegistry sessionRegistry;

    private final Map<String, Queue<Client>> clients = new ConcurrentHashMap<>();

    public void save(Client client) {
        if (nonNull(client) && nonNull(client.getTopic())) {
            clients.compute(client.getTopic(), (key, queue) -> {
                if (isNull(queue)) queue = new ConcurrentLinkedQueue<>();
                if (!queue.contains(client)) queue.offer(client);
                return queue;
            });
            log.info("Client={} saved in registry for topic={}", client.getId(), client.getTopic());
        } else {
            log.error("Client cannot be saved in registry");
        }
    }

    @Deprecated
    public Client getByClientIdOrThrow(String clientId) {
        return clients.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(client -> Objects.equals(clientId, client.getId()))
                .findFirst()
                .orElseThrow(() -> new ClientNotFoundException("Client %s not found".formatted(clientId)));
    }

    public List<Client> getAllAvailableClients() {
        return clients.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(client -> Objects.equals(ClientType.CONSUMER, client.getType()))
                .filter(Client::getIsAvailable)
                .toList();
    }

    public List<Client> getAllClients() {
        return clients.values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }
}
