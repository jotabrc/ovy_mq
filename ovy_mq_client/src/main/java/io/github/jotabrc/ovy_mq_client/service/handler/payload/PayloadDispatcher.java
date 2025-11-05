package io.github.jotabrc.ovy_mq_client.service.handler.payload;

import io.github.jotabrc.ovy_mq_client.service.handler.payload.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.provider.PayloadHandlerRegistryProvider;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadDispatcher {

    private final PayloadHandlerRegistryProvider payloadHandlerRegistryProvider;

    public void execute(Client client, Object payload, StompHeaders headers) {
        payloadHandlerRegistryProvider.getHandler(payload.getClass())
                .ifPresentOrElse(handler -> execute(handler, client, payload, headers),
                        () -> log.warn("No handler available for payload-class={}", payload.getClass()));
    }

    private <T> void execute(PayloadHandler<T> handler, Client client, Object payload, StompHeaders headers) {
        handler.handle(client, (T) payload, headers);
    }
}
