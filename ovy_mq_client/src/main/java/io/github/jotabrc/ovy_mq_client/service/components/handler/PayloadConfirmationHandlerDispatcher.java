package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.service.components.handler.payload.interfaces.PayloadConfirmationHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.provider.PayloadConfirmationHandlerRegistryProvider;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadConfirmationHandlerDispatcher {

    private final PayloadConfirmationHandlerRegistryProvider payloadConfirmationHandlerRegistryProvider;

    public void execute(SessionManager session,
                        Client client,
                        String destination,
                        Object payload) {
        payloadConfirmationHandlerRegistryProvider.getHandler(payload.getClass())
                .ifPresentOrElse(handler -> execute(handler, session, client, destination, payload),
                        () -> log.warn("No handler available for payload-class={}", payload.getClass()));
    }

    private <T> void execute(PayloadConfirmationHandler<T> handler,
                             SessionManager session,
                             Client client,
                             String destination,
                             Object payload) {
        handler.acknowledge(session, client, destination, (T) payload);
    }
}
