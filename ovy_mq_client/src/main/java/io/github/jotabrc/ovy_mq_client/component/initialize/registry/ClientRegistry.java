package io.github.jotabrc.ovy_mq_client.component.initialize.registry;

import io.github.jotabrc.ovy_mq_core.components.LockProcessor;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientRegistry {

    private final Map<String, Queue<Client>> clients = new ConcurrentHashMap<>();

    private final LockProcessor lockProcessor;

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
        Callable<Client> callable = () -> clients.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(client -> Objects.equals(clientId, client.getId()))
                .findFirst()
                .orElseThrow(() -> new OvyException.NotFound("Client %s not found".formatted(clientId)));
        return lockProcessor.getLockAndExecute(callable, null, null, clientId);
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
