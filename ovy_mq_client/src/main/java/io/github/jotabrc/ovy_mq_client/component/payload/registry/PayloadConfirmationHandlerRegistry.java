package io.github.jotabrc.ovy_mq_client.component.payload.registry;

import io.github.jotabrc.ovy_mq_client.component.payload.interfaces.PayloadConfirmationHandler;
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
            handlers.putIfAbsent(h.supports(), h);
        }
    }

    public Optional<PayloadConfirmationHandler<?>> getHandler(Class<?> classType) {
        return Optional.ofNullable(handlers.get(classType));
    }
}
