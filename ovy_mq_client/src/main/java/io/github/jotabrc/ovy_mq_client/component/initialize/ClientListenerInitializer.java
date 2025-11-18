package io.github.jotabrc.ovy_mq_client.component.initialize;

import io.github.jotabrc.ovy_mq_client.component.initialize.registry.ListenerConfigRegistry;
import io.github.jotabrc.ovy_mq_client.OvyListener;
import io.github.jotabrc.ovy_mq_client.component.initialize.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_core.components.MapCreator;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.defaults.Subscribe;
import io.github.jotabrc.ovy_mq_core.defaults.Value;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
import io.github.jotabrc.ovy_mq_core.domain.ListenerConfig;
import io.github.jotabrc.ovy_mq_core.components.factories.AbstractFactoryResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
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
    private final MapCreator mapCreator;

    private final Executor smallPoolExecutor;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        smallPoolExecutor.execute(() -> {
            log.info("Bean post-processing: bean={}", beanName);
            Class<?> beanClass = bean.getClass();

            for (Method method : beanClass.getMethods()) {
                OvyListener listener = AnnotationUtils.findAnnotation(method, OvyListener.class);

                if (nonNull(listener)) {
                    log.info("Listener: topic={} quantity={}", listener.topic(), listener.quantity());
                    AtomicInteger i = new AtomicInteger(1);
                    while (i.getAndIncrement() <= listener.quantity()) {
                        var definitions = mapCreator.create(mapCreator.createDto(Key.HEADER_TOPIC, listener.topic()),
                                mapCreator.createDto(Key.FACTORY_CLIENT_METHOD, method),
                                mapCreator.createDto(Key.FACTORY_CLIENT_BEAN_NAME, beanName),
                                mapCreator.createDto(Key.FACTORY_CLIENT_TIMEOUT, listener.timeout()),
                                mapCreator.createDto(Key.HEADER_CLIENT_TYPE, ClientType.CONSUMER),
                                mapCreator.createDto(Key.FACTORY_CLIENT_IS_AVAILABLE, true));
                        factoryResolver.create(definitions, Client.class)
                                .ifPresent(client -> {
                                    log.info("Creating client: replica={}/{} topic={} replica-config=[quantity={} max={} min={} step={} autoManage={} timeout={}ms]",
                                            i.get() + 1,
                                            listener.quantity(),
                                            listener.topic(),
                                            listener.quantity(),
                                            listener.max(),
                                            listener.min(),
                                            listener.step(),
                                            listener.autoManage(),
                                            listener.timeout());
                                    var sessionManagerDefinitions = mapCreator.create(mapCreator.createDto(Key.FACTORY_CLIENT_OBJECT, client),
                                            mapCreator.createDto(Key.FACTORY_SUBSCRIPTIONS, Subscribe.CONSUMER_SUBSCRIPTION.apply(client.getTopic())));
                                    sessionInitializer.createSessionAndConnect(client, sessionManagerDefinitions);
                                    clientRegistry.save(client);
                                });
                    }

                    var definitions = mapCreator.create(mapCreator.createDto(Key.HEADER_TOPIC, Value.ROLE_SERVER),
                            mapCreator.createDto(Key.FACTORY_CLIENT_TIMEOUT, listener.timeout()),
                            mapCreator.createDto(Key.FACTORY_REPLICA_QUANTITY, listener.quantity()),
                            mapCreator.createDto(Key.FACTORY_REPLICA_MAX, listener.max()),
                            mapCreator.createDto(Key.FACTORY_REPLICA_MIN, listener.min()),
                            mapCreator.createDto(Key.FACTORY_REPLICA_STEP, listener.step()),
                            mapCreator.createDto(Key.FACTORY_REPLICA_AUTO_MANAGE, listener.autoManage()));
                    factoryResolver.create(definitions, ListenerConfig.class).ifPresent(listenerConfigRegistry::save);
                }
            }
        });
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
