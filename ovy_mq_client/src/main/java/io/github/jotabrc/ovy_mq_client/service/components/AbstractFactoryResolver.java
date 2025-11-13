package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.service.components.interfaces.AbstractFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class AbstractFactoryResolver {

    private final Map<Class<?>, AbstractFactory<?, ?>> factories = new HashMap<>();

    @Autowired
    public AbstractFactoryResolver(List<AbstractFactory<?, ?>> factories) {
        for (AbstractFactory<?, ?> factory : factories) {
            this.factories.putIfAbsent(factory.supports(), factory);
        }
    }

    public <R, T> Optional<AbstractFactory<R, T>> getFactory(Class<R> classType, Class<T> MapValueType) {
        Optional<AbstractFactory<R, T>> factory = Optional.ofNullable((AbstractFactory<R, T>) factories.get(classType));
        if (factory.isEmpty()) log.warn("No factory available for class-type={}", StompHeaders.class);
        return factory;
    }
}
