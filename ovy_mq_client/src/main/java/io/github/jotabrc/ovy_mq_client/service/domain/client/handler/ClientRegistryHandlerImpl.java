package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.handler.ClientNotFoundException;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientRegistryHandler;
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
public class ClientRegistryHandlerImpl implements ClientRegistryHandler {

    private Map<String, Queue<Client>> clients = new ConcurrentHashMap<>();

    @Override
    public void save(Client client) {
        if (nonNull(client)
                && nonNull(client.getTopic())
                && nonNull(client.getClientSession())) {
            clients.compute(client.getTopic(), (key, queue) -> {
                if (isNull(queue)) queue = new ConcurrentLinkedQueue<>();
                if (!queue.contains(client)) queue.offer(client);
                return queue;
            });
            log.info("Client {} saved in registry for listeningTopic {}", client.getId(), client.getTopic());
        } else {
            log.error("Client cannot be saved in registry");
        }
    }

    @Override
    public Client getByClientIdOrThrow(String clientId) {
        return clients.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(client -> Objects.equals(clientId, client.getId()))
                .findFirst()
                .orElseThrow(() -> new ClientNotFoundException("Client %s not found".formatted(clientId)));
    }

    @Override
    public List<Client> getAllAvailableClients() {
        return clients.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(Client::getIsAvailable)
                .filter(client -> client.getClientSession().getSession().isConnected())
                .toList();
    }
}
