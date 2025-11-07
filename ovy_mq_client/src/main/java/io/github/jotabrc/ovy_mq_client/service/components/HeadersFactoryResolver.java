package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.service.components.interfaces.OvyHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class HeadersFactoryResolver {

    private final Map<Class<?>, OvyHeaders<?>> factories = new HashMap<>();

    @Autowired
    public HeadersFactoryResolver(List<OvyHeaders<?>> factories) {
        for (OvyHeaders<?> factory : factories) {
            this.factories.putIfAbsent(factory.supports(), factory);
        }
    }

    public Optional<OvyHeaders<?>> getFactory(Class<?> classType) {
        return Optional.of(factories.get(classType));
    }
}
