package io.github.jotabrc.ovy_mq_client.component.payload;

import io.github.jotabrc.ovy_mq_client.component.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.component.payload.interfaces.PayloadConfirmationHandler;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessagePayloadConfirmationHandler implements PayloadConfirmationHandler<MessagePayload> {

    private final ClientMessageDispatcher clientMessageDispatcher;

    @Override
    public void acknowledge(SessionManager session,
                            Client client,
                            String destination,
                            MessagePayload payload) {
        clientMessageDispatcher.send(client, client.getTopic(), destination, payload.cleanDataAndUpdateSuccessTo(true), session);
        client.setInboundMessageRequest(false);
    }

    @Override
    public Class<?> supports() {
        return MessagePayload.class;
    }
}
