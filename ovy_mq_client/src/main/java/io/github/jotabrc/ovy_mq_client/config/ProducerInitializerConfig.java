package io.github.jotabrc.ovy_mq_client.config;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.jotabrc.ovy_mq_client.facade.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.producer.StompOvyProducer;
import io.github.jotabrc.ovy_mq_client.producer.interfaces.OvyProducer;
import io.github.jotabrc.ovy_mq_client.session.initialize.impl.SessionInitializerResolver;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProducerInitializerConfig {

    @Bean
    public OvyProducer<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> stompOvyProduce(SessionInitializerResolver sessionInitializerResolver,
                                                                                                      AbstractFactoryResolver factoryResolver,
                                                                                                      ObjectProviderFacade objectProviderFacade) {
        DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                .add(OvyMqConstants.CLIENT_TYPE, ClientType.PRODUCER);
        return factoryResolver.create(definition, Client.class)
                .flatMap(client -> {

                    DefinitionMap handlerDefinition = objectProviderFacade.getDefinitionMap()
                            .add(OvyMqConstants.CLIENT_OBJECT, client)
                            .add(OvyMqConstants.SUBSCRIPTIONS, Subscribe.PRODUCER_SUBSCRIPTION)
                            .add(OvyMqConstants.MANAGERS, List.of(ManagerFactory.STOMP_HEALTH_CHECK));


                    return sessionInitializerResolver.get()
                            .flatMap(sessionInitializer -> sessionInitializer.createAndInitialize(client,
                                            handlerDefinition,
                                            new TypeReference<ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler>>() {
                                            })
                                    .map(StompOvyProducer::new));
                }).orElseThrow(() -> new IllegalStateException("Error while creating OvyProducer not available"));
    }
}
