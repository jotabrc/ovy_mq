package io.github.jotabrc.ovy_mq_client.session.initialize;

import io.github.jotabrc.ovy_mq_client.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.producer.OvyProducerImpl;
import io.github.jotabrc.ovy_mq_client.producer.interfaces.OvyProducer;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.util.Subscribe;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProducerClientInitializer {

    @Bean
    public OvyProducer ovyProducer(SessionInitializer sessionInitializer,
                                    AbstractFactoryResolver factoryResolver,
                                    ObjectProviderFacade objectProviderFacade) {
        DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                .add(OvyMqConstants.CLIENT_TYPE, ClientType.PRODUCER)
                .add(OvyMqConstants.SUBSCRIBED_TOPIC, OvyMqConstants.ROLE_SERVER);
        return factoryResolver.create(definition, Client.class)
                .map(client -> {
                    DefinitionMap sessionDefinition = objectProviderFacade.getDefinitionMap()
                            .add(OvyMqConstants.CLIENT_OBJECT, client)
                            .add(OvyMqConstants.SUBSCRIPTIONS, Subscribe.PRODUCER_SUBSCRIPTION);
                    return sessionInitializer.createSessionAndConnect(client, sessionDefinition)
                            .map(OvyProducerImpl::new).orElse(null);
                }).orElseThrow(() -> new IllegalStateException("Error while creating OvyProducer not available"));
    }
}
