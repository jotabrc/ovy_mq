package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.service.ClientRegistryContextHolder;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.ClientRegistrySelectHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientRegistrySelectHandlerImpl implements ClientRegistrySelectHandler {

    private final ClientRegistryContextHolder registry;

    @Override
    public synchronized Client handle(Client client) {
        log.info("Handling select for client with id={}", client.getId());
        for (var set : registry.getClients().values()) {
            for (var clientFound : set) {
                if (Objects.equals(client.getId(), clientFound.getId())) {
                    return clientFound;
                }
            }
        }
        return null;
    }
}
