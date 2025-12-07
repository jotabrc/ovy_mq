package io.github.jotabrc.ovy_mq_client.component.session.initialize;

import io.github.jotabrc.ovy_mq_client.component.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.component.producer.interfaces.ProducerTemplate;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Subscribe;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProducerClientInitializer implements ApplicationRunner {

    private final SessionInitializer sessionInitializer;
    private final AbstractFactoryResolver factoryResolver;
    private final ObjectProviderFacade objectProviderFacade;
    private final ProducerTemplate producerTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initialize();
    }

    private void initialize() {
        DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                .add(Key.HEADER_CLIENT_TYPE, ClientType.PRODUCER)
                .add(Key.HEADER_TOPIC, Value.ROLE_SERVER);
        factoryResolver.create(definition, Client.class)
                .ifPresent(client -> {
                    DefinitionMap sessionDefinition = objectProviderFacade.getDefinitionMap()
                            .add(Key.FACTORY_CLIENT_OBJECT, client)
                            .add(Key.FACTORY_SUBSCRIPTIONS, Subscribe.PRODUCER_SUBSCRIPTION);
                    sessionInitializer.createSessionAndConnect(client, sessionDefinition).ifPresent(producerTemplate::setSessionManager);
                });
    }
}
