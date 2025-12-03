package io.github.jotabrc.ovy_mq_client.component.initialize;

import io.github.jotabrc.ovy_mq_client.component.initialize.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_client.component.session.stomp.StompSessionHandler;
import io.github.jotabrc.ovy_mq_core.components.Definition;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class SessionInitializer {

    private final AbstractFactoryResolver abstractFactoryResolver;
    private final SessionRegistry sessionRegistry;
    private final Definition definition;

    public void createSessionAndConnect(Client client, DefinitionMap sessionDefinition) {
        log.info("Creating SessionManager and initializing: client={}", client.getId());
        abstractFactoryResolver.create(sessionDefinition, StompSessionHandler.class)
                .ifPresent(sessionManager -> {
                    sessionRegistry.addOrReplace(client.getId(), sessionManager);
                    sessionManager.initializeHandler();
                });
    }
}
