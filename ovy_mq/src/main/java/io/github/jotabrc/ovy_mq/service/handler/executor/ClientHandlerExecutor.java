package io.github.jotabrc.ovy_mq.service.handler.executor;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.service.handler.strategy.ClientRegistryStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ClientHandlerExecutor {

    public Client execute(ClientRegistryStrategy strategy, Client client) {
        return strategy.getHandler().handle(client);
    }
}
