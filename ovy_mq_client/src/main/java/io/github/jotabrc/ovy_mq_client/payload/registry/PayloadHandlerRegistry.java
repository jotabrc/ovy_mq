package io.github.jotabrc.ovy_mq_client.payload.registry;

import io.github.jotabrc.ovy_mq_client.payload.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class PayloadHandlerRegistry {

    private final Map<Class<?>, PayloadHandler<?>> handlers;

    public PayloadHandlerRegistry(List<PayloadHandler<?>> availableHandlers) {
        this.handlers = new HashMap<>();
        for (PayloadHandler<?> h : availableHandlers) {
            if (this.handlers.containsKey(h.supports())) throw new OvyException.ConfigurationError("Handler supporting=%s already exists".formatted(h.supports()));
            handlers.putIfAbsent(h.supports(), h);
        }
    }

    public Optional<PayloadHandler<?>> getHandler(Class<?> classType) {
        return Optional.ofNullable(handlers.get(classType));
    }
}
