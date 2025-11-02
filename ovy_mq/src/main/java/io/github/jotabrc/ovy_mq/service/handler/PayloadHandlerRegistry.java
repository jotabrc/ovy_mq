package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component
public class PayloadHandlerRegistry {

    private final Map<Class<?>, Map<PayloadDispatcherCommand,PayloadHandler<?>>> handlers;

    public PayloadHandlerRegistry(List<PayloadHandler<?>> availableHandlers) {
        this.handlers = new HashMap<>();
        for (PayloadHandler<?> h : availableHandlers) {
            handlers.compute(h.supports(), (key, map) -> {
                if (isNull(map)) map = new HashMap<>();
                map.putIfAbsent(h.command(), h);
                return map;
            });
        }
    }

    public Optional<PayloadHandler<?>> getHandler(Class<?> classType, PayloadDispatcherCommand command) {
        return Optional.ofNullable(handlers.get(classType).get(command));
    }
}
