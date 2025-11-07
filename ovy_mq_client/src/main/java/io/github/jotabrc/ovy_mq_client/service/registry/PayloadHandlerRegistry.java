package io.github.jotabrc.ovy_mq_client.service.registry;

import io.github.jotabrc.ovy_mq_client.service.components.handler.payload.interfaces.PayloadHandler;
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
            handlers.putIfAbsent(h.supports(), h);
        }
    }

    public Optional<PayloadHandler<?>> getHandler(Class<?> classType) {
        return Optional.ofNullable(handlers.get(classType));
    }
}
