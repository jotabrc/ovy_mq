package io.github.jotabrc.ovy_mq.service.handler;

import io.github.jotabrc.ovy_mq.service.handler.interfaces.PayloadHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadExecutor {

    private final PayloadHandlerRegistry payloadHandlerRegistry;

    public void execute(Object payload, PayloadHandlerCommand command) {
        payloadHandlerRegistry.getHandler(payload.getClass(), command)
                .ifPresentOrElse(handler -> execute(handler, payload),
                        () -> log.warn("No handler available for payload class {}", payload.getClass()));
    }

    private <T> void execute(PayloadHandler<T> handler, Object payload) {
        handler.handle((T) payload);
    }
}
