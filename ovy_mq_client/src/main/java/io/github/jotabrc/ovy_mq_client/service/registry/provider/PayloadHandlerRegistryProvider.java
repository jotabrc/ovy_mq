package io.github.jotabrc.ovy_mq_client.service.registry.provider;

import io.github.jotabrc.ovy_mq_client.service.components.handler.payload.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.PayloadHandlerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadHandlerRegistryProvider {

    private final PayloadHandlerRegistry payloadHandlerRegistry;

    public Optional<PayloadHandler<?>> getHandler(Class<?> classType) {
        return payloadHandlerRegistry.getHandler(classType);
    }
}
