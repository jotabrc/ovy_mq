package io.github.jotabrc.ovy_mq_client.payload.registry;

import io.github.jotabrc.ovy_mq_client.payload.handler.interfaces.PayloadConfirmationHandler;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class PayloadConfirmationHandlerRegistry {

    private final Map<Class<?>, PayloadConfirmationHandler<?>> handlers;

    public PayloadConfirmationHandlerRegistry(List<PayloadConfirmationHandler<?>> availableHandlers) {
        this.handlers = new HashMap<>();
        for (PayloadConfirmationHandler<?> h : availableHandlers) {
            if (this.handlers.containsKey(h.supports())) throw new OvyException.ConfigurationError("Handler supporting=%s already exists".formatted(h.supports()));
            handlers.putIfAbsent(h.supports(), h);
        }
    }

    public Optional<PayloadConfirmationHandler<?>> getHandler(Class<?> classType) {
        return Optional.ofNullable(handlers.get(classType));
    }
}
