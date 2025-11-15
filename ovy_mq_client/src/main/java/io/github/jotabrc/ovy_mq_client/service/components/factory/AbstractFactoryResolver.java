package io.github.jotabrc.ovy_mq_client.service.components.factory;

import io.github.jotabrc.ovy_mq_core.factories.interfaces.AbstractFactory;
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

    private final Map<Class<?>, AbstractFactory<?, ?>> factories;

    @Autowired
    public AbstractFactoryResolver(List<AbstractFactory<?, ?>> factories) {
        this.factories = factories.stream()
                .collect(Collectors.toMap(
                        AbstractFactory::supports,
                        factory -> factory
                ));
    }

    public <T, R> Optional<R> create(T dto, Class<R> type) {
        AbstractFactory<?, ?> factory = factories.get(dto.getClass());

        return Optional.ofNullable(factory)
                .map(f -> ((AbstractFactory<T, R>) f).create(dto))
                .stream()
                .findFirst();
    }
}
