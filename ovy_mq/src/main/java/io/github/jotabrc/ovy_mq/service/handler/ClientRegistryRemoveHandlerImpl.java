package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.DefaultClientKey;
import io.github.jotabrc.ovy_mq.service.ClientRegistryContextHolder;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryRemoveHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientRegistryRemoveHandlerImpl implements ClientRegistryRemoveHandler {

    private final ClientRegistryContextHolder registry;

    @Override
    public Client handle(Client client) {
        log.info("Handling client={} removal", client.getId());
        remove(client.getId());
        return null;
    }

    private synchronized void remove(String clientId) {
        AtomicBoolean isRemoved = new AtomicBoolean(false);
        if (nonNull(clientId) && !Objects.equals(DefaultClientKey.CLIENT_ID_NOT_FOUND.getValue(), clientId)) {
            findClientForRemoval(clientId, isRemoved);
        }
    }

    private void findClientForRemoval(String clientId, AtomicBoolean isRemoved) {
        registry.getClients().values()
                .stream()
                .takeWhile(set -> Objects.equals(Boolean.FALSE, isRemoved.get()))
                .forEach(set ->
                        removeClient(clientId, set, isRemoved)
                );
    }

    private void removeClient(String clientId, ConcurrentSkipListSet<Client> set, AtomicBoolean isRemoved) {
        isRemoved.set(set.removeIf(client -> Objects.equals(clientId, client.getId())));
    }
}
