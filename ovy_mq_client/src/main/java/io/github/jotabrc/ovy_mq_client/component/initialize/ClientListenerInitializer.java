package io.github.jotabrc.ovy_mq_client.component.initialize;

import io.github.jotabrc.ovy_mq_client.OvyListener;
import io.github.jotabrc.ovy_mq_client.component.initialize.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_client.component.initialize.registry.ListenerConfigRegistry;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Subscribe;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
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
    private final ListenerConfigRegistry listenerConfigRegistry;
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
                    createListenerConfig(listener);
                }
            }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private void createClient(String beanName, Method method, OvyListener listener, AtomicInteger i) {
        // todo simplify client creating inserting OvyListener instead of each value individually
        DefinitionMap definition = definitionProvider.getObject()
                .add(Key.HEADER_TOPIC, listener.topic())
                .add(Key.FACTORY_CLIENT_METHOD, method)
                .add(Key.FACTORY_CLIENT_BEAN_NAME, beanName)
                .add(Key.HEADER_CLIENT_TYPE, ClientType.CONSUMER)
                .add(Key.FACTORY_CLIENT_IS_AVAILABLE, true)
                //todo replica config quantity, min, max, ....
                .add(Key.FACTORY_PROCESSING_TIMEOUT, listener.processingTimeout())
                .add(Key.FACTORY_CLIENT_CONFIG_POLL_INITIAL_DELAY, listener.pollInitialDelay())
                .add(Key.FACTORY_CLIENT_CONFIG_POLL_FIXED_DELAY, listener.pollFixedDelay())
                .add(Key.FACTORY_CLIENT_CONFIG_HEALTH_CHECK_INITIAL_DELAY, listener.healthCheckInitialDelay())
                .add(Key.FACTORY_CLIENT_CONFIG_HEALTH_CHECK_FIXED_DELAY, listener.healthCheckFixedDelay())
                .add(Key.FACTORY_CLIENT_CONFIG_HEALTH_CHECK_EXPIRATION_TIME, listener.healthCheckExpirationTime())
                .add(Key.FACTORY_CLIENT_CONFIG_CONNECTION_MAX_RETRIES, listener.connectionMaxRetries())
                .add(Key.FACTORY_CLIENT_CONFIG_CONNECTION_TIMEOUT, listener.connectionTimeout())
                .add(Key.FACTORY_CLIENT_CONFIG_USE_GLOBAL_VALUES, listener.useGlobalValues());
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

    private void createListenerConfig(OvyListener listener) {
        //todo listener config auto manage dto
        DefinitionMap definition = definitionProvider.getObject()
                .add(Key.HEADER_TOPIC, Value.ROLE_SERVER)
                .add(Key.FACTORY_CLIENT_TIMEOUT, listener.processingTimeout())
                .add(Key.FACTORY_REPLICA_QUANTITY, listener.quantity())
                .add(Key.FACTORY_REPLICA_MAX, listener.max())
                .add(Key.FACTORY_REPLICA_MIN, listener.min())
                .add(Key.FACTORY_REPLICA_STEP, listener.step())
                .add(Key.FACTORY_REPLICA_AUTO_MANAGE, listener.autoManage())
                .add(Key.FACTORY_CLIENT_CONFIG_POLL_INITIAL_DELAY, listener.pollInitialDelay())
                .add(Key.FACTORY_CLIENT_CONFIG_POLL_FIXED_DELAY, listener.pollFixedDelay());
        factoryResolver.create(definition, ListenerConfig.class).ifPresent(listenerConfigRegistry::save);
    }
}
