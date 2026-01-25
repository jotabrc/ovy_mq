package io.github.jotabrc.ovy_mq_client.messaging.payload;

import io.github.jotabrc.ovy_mq_client.messaging.payload.handler.interfaces.PayloadConfirmationHandler;
import io.github.jotabrc.ovy_mq_client.messaging.payload.registry.PayloadConfirmationHandlerRegistry;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadConfirmationHandlerDispatcher {

    private final PayloadConfirmationHandlerRegistry payloadConfirmationHandlerRegistry;

    public void execute(Client client,
                        Object payload) {
        payloadConfirmationHandlerRegistry.getHandler(payload.getClass())
                .ifPresentOrElse(handler -> execute(handler, client, payload),
                        () -> log.warn("No handler available for payload-class={} operation=Payload-Acknowledge", payload.getClass()));
    }

    private <T> void execute(PayloadConfirmationHandler<T> handler,
                             Client client,
                             Object payload) {
        handler.acknowledge(client, (T) payload);
    }
}
