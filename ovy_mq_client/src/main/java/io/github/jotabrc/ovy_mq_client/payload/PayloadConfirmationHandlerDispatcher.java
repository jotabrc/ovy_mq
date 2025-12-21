package io.github.jotabrc.ovy_mq_client.payload;

import io.github.jotabrc.ovy_mq_client.payload.handler.interfaces.PayloadConfirmationHandler;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.payload.registry.PayloadConfirmationHandlerRegistry;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadConfirmationHandlerDispatcher {

    private final PayloadConfirmationHandlerRegistry payloadConfirmationHandlerRegistry;

    public void execute(SessionManager session,
                        Client client,
                        String destination,
                        Object payload) {
        payloadConfirmationHandlerRegistry.getHandler(payload.getClass())
                .ifPresentOrElse(handler -> execute(handler, session, client, destination, payload),
                        () -> log.warn("No handler available for payload-class={} operation=Payload-Acknowledge", payload.getClass()));
    }

    private <T> void execute(PayloadConfirmationHandler<T> handler,
                             SessionManager session,
                             Client client,
                             String destination,
                             Object payload) {
        handler.acknowledge(session, client, destination, (T) payload);
    }
}
