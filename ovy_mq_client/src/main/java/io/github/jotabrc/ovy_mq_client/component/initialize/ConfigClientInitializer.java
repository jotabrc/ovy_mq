package io.github.jotabrc.ovy_mq_client.component.initialize;

import io.github.jotabrc.ovy_mq_client.component.initialize.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_core.components.MapCreator;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Subscribe;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConfigClientInitializer implements ApplicationRunner {

    private final ClientRegistry clientRegistry;
    private final SessionInitializer sessionInitializer;
    private final AbstractFactoryResolver factoryResolver;
    private final MapCreator mapCreator;

    @Value("${ovymq.session.connection.config-client.timeout}")
    private Long timeout;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initialize();
    }

    private void initialize() {
        var definitions = mapCreator.create(mapCreator.createDto(Key.HEADER_CLIENT_TYPE, ClientType.CONFIGURER),
                mapCreator.createDto(Key.FACTORY_CLIENT_TIMEOUT, timeout),
                mapCreator.createDto(Key.HEADER_TOPIC, io.github.jotabrc.ovy_mq_core.defaults.Value.ROLE_SERVER));
        factoryResolver.create(definitions, Client.class)
                .ifPresent(client -> {
                    var sessionManagerDefinitions = mapCreator.create(mapCreator.createDto(Key.FACTORY_CLIENT_OBJECT, client),
                            mapCreator.createDto(Key.FACTORY_SUBSCRIPTIONS, Subscribe.CONFIGURER_SUBSCRIPTION));
                    sessionInitializer.createSessionAndConnect(client, sessionManagerDefinitions);
                    clientRegistry.save(client);
                });
    }
}
