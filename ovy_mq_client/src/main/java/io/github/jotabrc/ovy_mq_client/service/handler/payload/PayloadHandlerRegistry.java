package io.github.jotabrc.ovy_mq_client.service.handler.payload;

import io.github.jotabrc.ovy_mq_client.service.handler.payload.interfaces.PayloadHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PayloadHandlerRegistry {

    private final Map<Class<?>, PayloadHandler<?>> handlers;

    public PayloadHandlerRegistry(List<PayloadHandler<?>> availableHandlers) {
        this.handlers = new HashMap<>();
        for (PayloadHandler<?> h : availableHandlers) {
            handlers.putIfAbsent(h.supports(), h);
        }
    }

    public Optional<PayloadHandler<?>> getHandler(Class<?> classType) {
        return Optional.ofNullable(handlers.get(classType));
    }
}
