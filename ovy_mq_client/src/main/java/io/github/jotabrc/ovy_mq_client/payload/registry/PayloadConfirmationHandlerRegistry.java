package io.github.jotabrc.ovy_mq_client.payload.registry;

import io.github.jotabrc.ovy_mq_client.payload.handler.interfaces.PayloadConfirmationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class PayloadConfirmationHandlerRegistry {

    private final Map<Class<?>, PayloadConfirmationHandler<?>> handlers = new HashMap<>();

    public PayloadConfirmationHandlerRegistry(List<PayloadConfirmationHandler<?>> availableHandlers) {
        for (PayloadConfirmationHandler<?> h : availableHandlers) {
            handlers.putIfAbsent(h.supports(), h);
        }
    }

    public Optional<PayloadConfirmationHandler<?>> getHandler(Class<?> classType) {
        return Optional.ofNullable(handlers.get(classType));
    }
}
