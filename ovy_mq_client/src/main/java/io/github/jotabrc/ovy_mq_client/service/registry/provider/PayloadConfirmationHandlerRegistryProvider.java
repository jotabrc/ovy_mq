package io.github.jotabrc.ovy_mq_client.service.registry.provider;

import io.github.jotabrc.ovy_mq_client.service.components.handler.payload.interfaces.PayloadConfirmationHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.PayloadConfirmationHandlerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadConfirmationHandlerRegistryProvider {

    private final PayloadConfirmationHandlerRegistry payloadConfirmationHandlerRegistry;

    public Optional<PayloadConfirmationHandler<?>> getHandler(Class<?> classType) {
        return payloadConfirmationHandlerRegistry.getHandler(classType);
    }
}
