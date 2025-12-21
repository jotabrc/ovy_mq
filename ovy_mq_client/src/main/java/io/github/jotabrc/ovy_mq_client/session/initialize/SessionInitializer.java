package io.github.jotabrc.ovy_mq_client.session.initialize;

import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.session.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_client.session.stomp.StompSessionHandler;
import io.github.jotabrc.ovy_mq_core.components.DefinitionMapImpl;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class SessionInitializer {

    private final AbstractFactoryResolver abstractFactoryResolver;
    private final SessionRegistry sessionRegistry;
    private final DefinitionMapImpl definitionMapImpl;

    public Optional<SessionManager> createSessionAndConnect(Client client, DefinitionMap sessionDefinition) {
        log.info("Creating SessionManager and initializing: client={}", client.getId());
        return abstractFactoryResolver.create(sessionDefinition, StompSessionHandler.class)
                .map(sessionManager -> {
                    if (!Objects.equals(ClientType.PRODUCER, client.getType())) {
                        sessionRegistry.addOrReplace(client.getId(), sessionManager);
                    }
                    sessionManager.initializeHandler();
                    return sessionManager;
                });
    }
}
