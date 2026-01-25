package io.github.jotabrc.ovy_mq_client.facade;

import io.github.jotabrc.ovy_mq_client.messaging.payload.PayloadConfirmationHandlerDispatcher;
import io.github.jotabrc.ovy_mq_client.messaging.payload.PayloadHandlerDispatcher;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
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

    public void acknowledgePayload(Client client, Object payload) {
        payloadConfirmationHandlerDispatcher.execute(client, payload);
    }
}
