package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.service.components.interfaces.OvyHeaderFactory;
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
public class HeadersFactoryResolver {

    private final Map<Class<?>, OvyHeaderFactory<?>> factories = new HashMap<>();

    @Autowired
    public HeadersFactoryResolver(List<OvyHeaderFactory<?>> factories) {
        for (OvyHeaderFactory<?> factory : factories) {
            this.factories.putIfAbsent(factory.supports(), factory);
        }
    }

    public <R> Optional<OvyHeaderFactory<R>> getFactory(Class<R> classType) {
        Optional<OvyHeaderFactory<R>> factory = Optional.ofNullable((OvyHeaderFactory<R>) factories.get(classType));
        if (factory.isEmpty()) log.warn("No factory available for class-type={}", StompHeaders.class);
        return factory;
    }
}
