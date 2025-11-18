package io.github.jotabrc.ovy_mq_client.component.initialize;

import io.github.jotabrc.ovy_mq_client.component.session.StompSessionHandler;
import io.github.jotabrc.ovy_mq_client.component.initialize.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_core.components.MapCreator;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.factories.AbstractFactoryResolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class SessionInitializer {

    private final AbstractFactoryResolver abstractFactoryResolver;
    private final SessionRegistry sessionRegistry;
    private final MapCreator mapCreator;

    public void createSessionAndConnect(Client client, Map<String, Object> sessionManagerDefinitions) {
        log.info("Creating SessionManager and initializing: client={}", client.getId());
        abstractFactoryResolver.create(sessionManagerDefinitions, StompSessionHandler.class)
                .ifPresent(sessionManager -> {
                    sessionRegistry.addOrReplace(client.getId(), sessionManager);
                    sessionManager.initialize();
                });
    }
}
