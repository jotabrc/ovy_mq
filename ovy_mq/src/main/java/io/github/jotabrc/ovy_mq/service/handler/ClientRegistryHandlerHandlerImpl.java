package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.DefaultClientKey;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryHandler;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@AllArgsConstructor
@Component
public class ClientRegistryHandlerHandlerImpl implements ClientRegistryHandler {

    private final ConcurrentHashMap<String, ConcurrentSkipListSet<Client>> clients = new ConcurrentHashMap<>();

    @Async
    @Override
    public synchronized void updateClientList(Client client) {
        updateClientListOperation(client);
    }

    private void updateClientListOperation(Client client) {
        if (nonNull(client) && nonNull(client.getId()) && isNull(clients.get(client.getId()))) {
            clients.compute(client.getTopic(), (clientId, set) -> {
                if (isNull(set)) set = new ConcurrentSkipListSet<>(getComparator());
                else set.remove(client);

                set.add(client);
                return set;
            });
        }
    }

    private Comparator<Client> getComparator() {
        return Comparator.comparing(Client::getIsAvailable).reversed();
    }

    @Async
    @Override
    public synchronized void remove(String clientId) {
        AtomicBoolean isRemoved = new AtomicBoolean(false);
        if (nonNull(clientId) && !Objects.equals(DefaultClientKey.CLIENT_ID_NOT_FOUND.getValue(), clientId)) {
            findClientForRemoval(clientId, isRemoved);
        }
    }

    private void findClientForRemoval(String clientId, AtomicBoolean isRemoved) {
        clients.values()
                .stream()
                .takeWhile(set -> Objects.equals(Boolean.FALSE, isRemoved.get()))
                .forEach(set ->
                        removeClient(clientId, set, isRemoved)
                );
    }

    private void removeClient(String clientId, ConcurrentSkipListSet<Client> set, AtomicBoolean isRemoved) {
        isRemoved.set(set.removeIf(client -> Objects.equals(clientId, client.getId())));
    }

    @Override
    public Client findClientById(String clientId) {
        for (var set : clients.values()) {
            for (var consumer : set) {
                if (Objects.equals(clientId, consumer.getId())) return consumer;
            }
        }
        return null;
    }

    @Override
    public Client findLeastRecentlyUsedClientByTopic(String topic) {
        Client client = clients.get(topic).getFirst();
        if (nonNull(client) && client.getIsAvailable()) {
            return client;
        }
        return null;
    }

    @Override
    public List<Client> findOneAvailableClientPerTopic() {
        List<Client> availableClients = new ArrayList<>();
        for (var set : clients.values()) {
            for (var consumer : set) {
                if (consumer.getIsAvailable()) {
                    availableClients.add(consumer);
                    break;
                }
            }
        }
        return availableClients;
    }

    @Override
    public List<Client> findAllAvailableClients() {
        return clients.values()
                .stream().flatMap(set -> set.stream()
                        .filter(Client::getIsAvailable)
                ).toList();
    }

    @Override
    public Integer isThereAnyAvailableClientForTopic(String topic) {
        return clients.get(topic).size();
    }
}
