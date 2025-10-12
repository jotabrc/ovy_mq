package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.service.ClientRegistryContextHolder;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistryHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@AllArgsConstructor
@Component
public class ClientRegistryUpsertHandler implements ClientRegistryHandler {

    private final ClientRegistryContextHolder registry;

    @Override
    public synchronized Client handle(Client client) {
        if (nonNull(client) && nonNull(client.getId()) && isNull(registry.getClients().get(client.getId()))) {
            log.info("Handling client={} upsert with topic={}", client.getId(), client.getTopic());
            registry.getClients().compute(client.getTopic(), (clientId, set) -> {
                if (isNull(set)) set = new ConcurrentSkipListSet<>(getComparator());
                else set.remove(client);

                set.add(client);
                return set;
            });
        }
        return client;
    }

    private Comparator<Client> getComparator() {
        return Comparator.comparing(Client::getIsAvailable)
                .reversed();
    }
}
