package io.github.jotabrc.ovy_mq_core.components.factories;

import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.ClientFactoryStrategy;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ClientFactoryStrategyResolver {

    private final Map<ClientType, ClientFactoryStrategy> strategies = new HashMap<>();

    public ClientFactoryStrategyResolver(List<ClientFactoryStrategy> strategies) {
        strategies.forEach(clientFactoryStrategy ->
                this.strategies.putIfAbsent(clientFactoryStrategy.supports(), clientFactoryStrategy));
    }

    public Client create(DefinitionMap definition) {
        Optional<ClientFactoryStrategy> strategy = get(definition.extract(OvyMqConstants.CLIENT_TYPE, ClientType.class));
        return strategy
                .map(clientFactoryStrategy -> clientFactoryStrategy.create(definition))
                .orElseThrow(() -> new OvyException.ConfigurationError("Unable to create factory for=%s".formatted(definition.extract(OvyMqConstants.CLIENT_TYPE, ClientType.class))));
    }

    private Optional<ClientFactoryStrategy> get(ClientType clientType) {
        return Optional.ofNullable(strategies.get(clientType));
    }
}
