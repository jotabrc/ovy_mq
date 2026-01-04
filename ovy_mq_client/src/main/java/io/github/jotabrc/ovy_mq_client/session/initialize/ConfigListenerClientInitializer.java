package io.github.jotabrc.ovy_mq_client.session.initialize;

import io.github.jotabrc.ovy_mq_client.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.util.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConfigListenerClientInitializer implements ApplicationRunner {

    private final SessionInitializerResolver sessionInitializerResolver;
    private final ClientRegistry clientRegistry;
    private final AbstractFactoryResolver factoryResolver;
    private final ObjectProvider<DefinitionMap> definitionProvider;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initialize();
    }

    private void initialize() {
        DefinitionMap definition = definitionProvider.getObject()
                .add(OvyMqConstants.CLIENT_TYPE, ClientType.CONFIGURER)
                .add(OvyMqConstants.SUBSCRIBED_TOPIC, OvyMqConstants.ROLE_SERVER);
        factoryResolver.create(definition, Client.class)
                .ifPresent(client -> {
                    DefinitionMap sessionDefinition = definitionProvider.getObject()
                            .add(OvyMqConstants.CLIENT_OBJECT, client)
                            .add(OvyMqConstants.SUBSCRIPTIONS, Subscribe.CONFIGURER_SUBSCRIPTION);
                    sessionInitializerResolver.get()
                            .ifPresent(sessionInitializer -> sessionInitializer.createSessionAndConnect(client, sessionDefinition));
                    clientRegistry.save(client);
                });
    }
}
