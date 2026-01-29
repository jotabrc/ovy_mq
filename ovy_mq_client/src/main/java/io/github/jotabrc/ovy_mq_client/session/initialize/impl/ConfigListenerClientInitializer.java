package io.github.jotabrc.ovy_mq_client.session.initialize.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.jotabrc.ovy_mq_client.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.ManagerFactory;
import io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.stomp_handler.StompClientSessionHandler;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.util.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.util.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConfigListenerClientInitializer implements ApplicationRunner {

    private final SessionInitializerResolver sessionInitializerResolver;
    private final ClientRegistry clientRegistry;
    private final AbstractFactoryResolver factoryResolver;
    private final ObjectProvider<DefinitionMap> definitionProvider;

    @Override
    public void run(ApplicationArguments args) {
        initialize();
    }

    private void initialize() {
        DefinitionMap definition = definitionProvider.getObject()
                .add(OvyMqConstants.CLIENT_TYPE, ClientType.CONFIGURER);
        factoryResolver.create(definition, Client.class)
                .ifPresent(client -> {
                    DefinitionMap sessionDefinition = definitionProvider.getObject()
                            .add(OvyMqConstants.CLIENT_OBJECT, client)
                            .add(OvyMqConstants.SUBSCRIPTIONS, Subscribe.CONFIGURER_SUBSCRIPTION)
                            .add(OvyMqConstants.MANAGERS, List.of(ManagerFactory.STOMP_HEALTH_CHECK));
                    sessionInitializerResolver.get()
                            .ifPresent(sessionInitializer -> sessionInitializer.createAndInitialize(client, sessionDefinition, new TypeReference<ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler>>() {
                            }));
                    clientRegistry.save(client);
                });
    }
}
