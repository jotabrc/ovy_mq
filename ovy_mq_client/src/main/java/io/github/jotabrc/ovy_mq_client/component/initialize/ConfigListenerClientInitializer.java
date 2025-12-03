package io.github.jotabrc.ovy_mq_client.component.initialize;

import io.github.jotabrc.ovy_mq_client.component.initialize.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Subscribe;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
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

    private final ClientRegistry clientRegistry;
    private final SessionInitializer sessionInitializer;
    private final AbstractFactoryResolver factoryResolver;
    private final ObjectProvider<DefinitionMap> definitionProvider;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initialize();
    }

    private void initialize() {
        DefinitionMap definition = definitionProvider.getObject()
                .add(Key.HEADER_CLIENT_TYPE, ClientType.CONFIGURER)
                .add(Key.HEADER_TOPIC, Value.ROLE_SERVER);
        factoryResolver.create(definition, Client.class)
                .ifPresent(client -> {
                    DefinitionMap sessionDefinition = definitionProvider.getObject()
                            .add(Key.FACTORY_CLIENT_OBJECT, client)
                            .add(Key.FACTORY_SUBSCRIPTIONS, Subscribe.CONFIGURER_SUBSCRIPTION);
                    sessionInitializer.createSessionAndConnect(client, sessionDefinition);
                    clientRegistry.save(client);
                });
    }
}
