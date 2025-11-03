package io.github.jotabrc.ovy_mq_client.service.registry;

import io.github.jotabrc.ovy_mq_client.service.handler.payload.interfaces.PayloadHandler;
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
