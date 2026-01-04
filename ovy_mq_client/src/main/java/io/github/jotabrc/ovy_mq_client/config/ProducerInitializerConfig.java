package io.github.jotabrc.ovy_mq_client.config;

import io.github.jotabrc.ovy_mq_client.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.producer.StompOvyProducer;
import io.github.jotabrc.ovy_mq_client.producer.interfaces.OvyProducer;
import io.github.jotabrc.ovy_mq_client.session.initialize.SessionInitializerResolver;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.util.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProducerInitializerConfig {

    @Bean
    public OvyProducer stompOvyProduce(SessionInitializerResolver sessionInitializerResolver,
                                       AbstractFactoryResolver factoryResolver,
                                       ObjectProviderFacade objectProviderFacade) {
        DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                .add(OvyMqConstants.CLIENT_TYPE, ClientType.PRODUCER)
                .add(OvyMqConstants.SUBSCRIBED_TOPIC, OvyMqConstants.ROLE_SERVER);
        return factoryResolver.create(definition, Client.class)
                .flatMap(client -> {
                    DefinitionMap sessionDefinition = objectProviderFacade.getDefinitionMap()
                            .add(OvyMqConstants.CLIENT_OBJECT, client)
                            .add(OvyMqConstants.SUBSCRIPTIONS, Subscribe.PRODUCER_SUBSCRIPTION);
                    return sessionInitializerResolver.get()
                            .flatMap(sessionInitializer -> sessionInitializer.createSessionAndConnect(client, sessionDefinition)
                            .map(StompOvyProducer::new));
                }).orElseThrow(() -> new IllegalStateException("Error while creating OvyProducer not available"));
    }
}
