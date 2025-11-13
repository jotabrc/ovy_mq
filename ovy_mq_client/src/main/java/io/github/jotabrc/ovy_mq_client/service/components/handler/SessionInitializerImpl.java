package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_client.service.components.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionInitializer;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.service.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class SessionInitializerImpl implements SessionInitializer {

    private final AbstractFactoryResolver abstractFactoryResolver;
    private final SessionRegistry sessionRegistry;



    @Override
    public void createSessionAndConnect(Client client) {
        abstractFactoryResolver.getFactory(ConsumerStompSessionHandler.class, Client.class).ifPresent(factory -> {
            SessionManager sessionManager = factory.create(Map.of(Key.FACTORY_CLIENT, client));
            sessionRegistry.addOrReplace(client.getId(), sessionManager);
            sessionManager.initialize();
        });
    }
}
