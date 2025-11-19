package io.github.jotabrc.ovy_mq_core.components.factories;

import io.github.jotabrc.ovy_mq_core.components.factories.interfaces.AbstractFactory;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AbstractFactoryResolver {

    private final Map<Class<?>, AbstractFactory<?>> factories;

    @Autowired
    public AbstractFactoryResolver(List<AbstractFactory<?>> factories) {
        this.factories = factories.stream()
                .collect(Collectors.toMap(
                        AbstractFactory::supports,
                        factory -> factory
                ));
    }

    public <R> Optional<R> create(DefinitionMap definition, Class<R> returningType) {
        AbstractFactory<?> factory = factories.get(returningType);

        return Optional.ofNullable(factory)
                .map(f -> ((AbstractFactory<R>) f).create(definition))
                .stream()
                .findFirst();
    }
}
