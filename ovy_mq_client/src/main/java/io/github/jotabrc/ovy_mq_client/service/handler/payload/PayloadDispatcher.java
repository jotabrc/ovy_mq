package io.github.jotabrc.ovy_mq_client.service.handler.payload;

import io.github.jotabrc.ovy_mq_client.service.handler.payload.interfaces.PayloadHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadDispatcher {

    private final PayloadHandlerRegistry payloadHandlerRegistry;

    public void execute(String clientId, Object payload, StompHeaders headers) {
        payloadHandlerRegistry.getHandler(payload.getClass())
                .ifPresentOrElse(handler -> execute(handler, clientId, payload, headers),
                        () -> log.warn("No handler available for payload-class={}", payload.getClass()));
    }

    private <T> void execute(PayloadHandler<T> handler, String clientId, Object payload, StompHeaders headers) {
        handler.handle(clientId, (T) payload, headers);
    }
}
