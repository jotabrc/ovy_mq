package io.github.jotabrc.ovy_mq_client.payload;

import io.github.jotabrc.ovy_mq_client.payload.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_client.payload.registry.PayloadHandlerRegistry;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadHandlerDispatcher {

    private final PayloadHandlerRegistry payloadHandlerRegistry;

    public void execute(Client client, Object payload, StompHeaders headers) {
        payloadHandlerRegistry.getHandler(payload.getClass())
                .ifPresentOrElse(handler -> execute(handler, client, payload, headers),
                        () -> log.warn("No handler available for payload-class={}", payload.getClass()));
    }

    private <T> void execute(PayloadHandler<T> handler, Client client, Object payload, StompHeaders headers) {
        handler.handle(client, (T) payload, headers);
    }
}
