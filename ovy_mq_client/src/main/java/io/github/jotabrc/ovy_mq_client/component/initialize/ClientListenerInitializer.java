package io.github.jotabrc.ovy_mq_client.component.initialize;

import io.github.jotabrc.ovy_mq_client.component.initialize.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Subscribe;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.client.OvyListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientListenerInitializer implements BeanPostProcessor {

    private final SessionInitializer sessionInitializer;
    private final ClientRegistry clientRegistry;
    private final AbstractFactoryResolver factoryResolver;
    private final ObjectProvider<DefinitionMap> definitionProvider;

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
        // todo simplify client creating inserting OvyListener instead of each value individually
        DefinitionMap definition = definitionProvider.getObject()
                .add(Key.FACTORY_CLIENT_METHOD, method)
                .add(Key.FACTORY_CLIENT_BEAN_NAME, beanName)
                .add(Key.HEADER_CLIENT_TYPE, ClientType.CONSUMER)
                .add(Key.FACTORY_CLIENT_IS_AVAILABLE, true)
                .add(Key.FACTORY_OVY_LISTENER, listener);
        factoryResolver.create(definition, Client.class)
                .ifPresent(client -> {
                    log.info("Creating client: replica={}/{} topic={}", i.get(), listener.quantity(), listener.topic());
                    createClientSessionManager(client);
                    clientRegistry.save(client);
                });
    }

    private void createClientSessionManager(Client client) {
        DefinitionMap sessionDefinition = definitionProvider.getObject()
                .add(Key.FACTORY_CLIENT_OBJECT, client)
                .add(Key.FACTORY_SUBSCRIPTIONS, Subscribe.CONSUMER_SUBSCRIPTION.apply(client.getTopic()));
        sessionInitializer.createSessionAndConnect(client, sessionDefinition);
    }
}
