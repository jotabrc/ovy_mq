package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ClientRegistryStrategyExecutor {

    public Client execute(ClientRegistryStrategy strategy, Client client) {
        return strategy.getHandler().handle(client);
    }
}
