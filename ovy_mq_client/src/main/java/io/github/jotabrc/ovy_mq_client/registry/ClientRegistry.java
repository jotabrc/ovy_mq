package io.github.jotabrc.ovy_mq_client.registry;

import io.github.jotabrc.ovy_mq_core.components.LockProcessor;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

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

    public List<Client> getClientsByTopic(String topic) {
        Callable<List<Client>> callable = () -> clients.values().stream()
                .flatMap(Collection::stream)
                .filter(client -> Objects.equals(topic, client.getTopic()))
                .collect(Collectors.toCollection(ArrayList::new));
        return lockProcessor.getLockAndExecute(callable, topic, null, null);
    }
}