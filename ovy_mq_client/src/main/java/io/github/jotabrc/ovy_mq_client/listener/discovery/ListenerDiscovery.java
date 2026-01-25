package io.github.jotabrc.ovy_mq_client.listener.discovery;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.jotabrc.ovy_mq_client.facade.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_client.session.initialize.SessionInitializerResolver;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.ManagerFactory;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.stomp_handler.StompClientSessionHandler;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.client.OvyListener;
import io.github.jotabrc.ovy_mq_core.util.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ListenerDiscovery implements BeanPostProcessor {

    private final SessionInitializerResolver sessionInitializerResolver;
    private final ClientRegistry clientRegistry;
    private final AbstractFactoryResolver factoryResolver;
    private final ObjectProviderFacade objectProviderFacade;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        for (Method method : beanClass.getMethods()) {
            OvyListener listener = AnnotationUtils.findAnnotation(method, OvyListener.class);
            if (nonNull(listener)) {
                log.info("Listener found: topic={} replicas={}", listener.topic(), listener.quantity());
                AtomicInteger i = new AtomicInteger(0);
                while (i.incrementAndGet() <= listener.quantity()) {
                    createClient(beanName, method, listener, i);
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private void createClient(String beanName, Method method, OvyListener listener, AtomicInteger i) {
        DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                .add(OvyMqConstants.CLIENT_METHOD, method)
                .add(OvyMqConstants.CLIENT_BEAN_NAME, beanName)
                .add(OvyMqConstants.CLIENT_TYPE, ClientType.CONSUMER)
                .add(OvyMqConstants.OVY_LISTENER, listener);
        factoryResolver.create(definition, Client.class)
                .ifPresent(client -> {
                    log.info("Creating client: replica={}/{} topic={}", i.get(), listener.quantity(), listener.topic());
                    createClientManagersAndInitialize(client);
                    clientRegistry.save(client);
                });
    }

    private void createClientManagersAndInitialize(Client client) {
        DefinitionMap definition = objectProviderFacade.getDefinitionMap()
                .add(OvyMqConstants.CLIENT_OBJECT, client)
                .add(OvyMqConstants.SUBSCRIPTIONS, Subscribe.CONSUMER_SUBSCRIPTION.apply(client.getTopic()))
                .add(OvyMqConstants.MANAGERS, List.of(ManagerFactory.HEALTH_CHECK, ManagerFactory.LISTENER_POLL));
        sessionInitializerResolver.get()
                .ifPresent(clientHandlerInitializer -> clientHandlerInitializer.createAndInitialize(client,
                        definition,
                        new TypeReference<ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler>>() {
                        }));
    }
}
