package io.github.jotabrc.ovy_mq_client.component;

import io.github.jotabrc.ovy_mq_client.component.payload.PayloadConfirmationHandlerDispatcher;
import io.github.jotabrc.ovy_mq_client.component.payload.PayloadHandlerDispatcher;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DispatcherFacade {

    private final PayloadHandlerDispatcher payloadHandlerDispatcher;
    private final PayloadConfirmationHandlerDispatcher payloadConfirmationHandlerDispatcher;

    public void handlePayload(Client client, Object payload, StompHeaders headers) {
        payloadHandlerDispatcher.execute(client, payload, headers);
    }

    public void acknowledgePayload(SessionManager sessionManager, Client client, String destination, Object payload) {
        payloadConfirmationHandlerDispatcher.execute(sessionManager, client, destination, payload);
    }
}
