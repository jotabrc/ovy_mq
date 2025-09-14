package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.DefaultClientKey;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@AllArgsConstructor
@Component
public class ConsumerRegistryImpl implements ConsumerRegistry {

    private final ConcurrentHashMap<String, ConcurrentSkipListSet<Client>> clients = new ConcurrentHashMap<>();

    @Override
    public boolean updateClientList(Client client) {
        return add(client);
    }

    private boolean add(Client client) {
        if (nonNull(client) && nonNull(client.getId()) && isNull(clients.get(client.getId()))) {
            clients.compute(client.getListeningTopic(), (clientId, set) -> {
                if (isNull(set)) set = new ConcurrentSkipListSet<>(getComparator());
                else set.remove(client);

                set.add(client);
                return set;
            });
            return true;
        }
        return false;
    }

    private Comparator<Client> getComparator() {
        return Comparator.comparing(Client::getIsAvailable).reversed()
                .thenComparing(Client::getLastUsed);
    }

    @Override
    public boolean remove(String clientId) {
        AtomicBoolean isRemoved = new AtomicBoolean(false);
        if (nonNull(clientId) && !Objects.equals(DefaultClientKey.CLIENT_ID_NOT_FOUND.getValue(), clientId)) {
            findClientForRemoval(clientId, isRemoved);
        }
        return isRemoved.get();
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
    public synchronized Client obtainConsumerAvailableInOrderOfOlderUsedFirst(String topic) {
        Client client = clients.get(topic).getFirst();
        if (nonNull(client) && client.getIsAvailable()) {
            client.updateStatus();
            updateClientList(client);
        }
        return client;
    }
}
