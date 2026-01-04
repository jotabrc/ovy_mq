package io.github.jotabrc.ovy_mq_client.session.initialize;

import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_client.session.initialize.interfaces.SessionInitializer;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_client.session.stomp.StompSessionHandler;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompClientSessionInitializer implements SessionInitializer {

    private final AbstractFactoryResolver abstractFactoryResolver;
    private final SessionRegistry sessionRegistry;

    @Override
    public Optional<SessionManager> createSessionAndConnect(Client client, DefinitionMap sessionDefinition) {
        log.info("Creating SessionManager and initializing: client={}", client.getId());
        return abstractFactoryResolver.create(sessionDefinition, StompSessionHandler.class)
                .map(sessionManager -> {
                    sessionRegistry.addOrReplace(client.getId(), sessionManager);
                    sessionManager.initializeManagers();
                    return sessionManager;
                });
    }

    @Override
    public SessionType supports() {
        return SessionType.STOMP;
    }
}