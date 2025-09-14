package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.Consumer;
import io.github.jotabrc.ovy_mq.domain.DefaultClientKey;
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
public class ConsumerRegistryImpl implements ConsumerRegistry {

    private final ConcurrentHashMap<String, ConcurrentSkipListSet<Consumer>> clients = new ConcurrentHashMap<>();

    @Async
    @Override
    public synchronized void updateClientList(Consumer consumer) {
        updateClientListOperation(consumer);
    }

    private void updateClientListOperation(Consumer consumer) {
        if (nonNull(consumer) && nonNull(consumer.getId()) && isNull(clients.get(consumer.getId()))) {
            clients.compute(consumer.getListeningTopic(), (clientId, set) -> {
                if (isNull(set)) set = new ConcurrentSkipListSet<>(getComparator());
                else set.remove(consumer);

                set.add(consumer);
                return set;
            });
        }
    }

    private Comparator<Consumer> getComparator() {
        return Comparator.comparing(Consumer::getIsAvailable).reversed()
                .thenComparing(Consumer::getLastUsed);
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

    private void removeClient(String clientId, ConcurrentSkipListSet<Consumer> set, AtomicBoolean isRemoved) {
        isRemoved.set(set.removeIf(client -> Objects.equals(clientId, client.getId())));
    }

    @Override
    public Consumer findConsumerByClientId(String clientId) {
        for (var set : clients.values()) {
            for (var consumer : set) {
                if (Objects.equals(clientId, consumer.getId())) return consumer;
            }
        }
        return null;
    }

    @Override
    public Consumer findLeastRecentlyUsedConsumerAvailableForTopic(String topic) {
        Consumer consumer = clients.get(topic).getFirst();
        if (nonNull(consumer) && consumer.getIsAvailable()) {
            consumer.updateStatus();
            updateClientListOperation(consumer);
            return consumer;
        }
        return null;
    }

    @Override
    public List<Consumer> findOneAvailableConsumersPerTopic() {
        List<Consumer> availableConsumers = new ArrayList<>();
        for (var set : clients.values()) {
            for (var consumer : set) {
                if (consumer.getIsAvailable()) {
                    availableConsumers.add(consumer);
                    break;
                }
            }
        }
        return availableConsumers;
    }

    @Override
    public List<Consumer> findAllAvailableConsumers() {
        return clients.values()
                .stream().flatMap(set -> set.stream()
                        .filter(Consumer::getIsAvailable)
                ).toList();
    }

    @Override
    public Integer isThereAnyAvailableConsumerForTopic(String topic) {
        return clients.get(topic).size();
    }
}
