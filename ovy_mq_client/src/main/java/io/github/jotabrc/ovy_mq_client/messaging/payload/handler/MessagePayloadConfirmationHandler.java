package io.github.jotabrc.ovy_mq_client.messaging.payload.handler;

import io.github.jotabrc.ovy_mq_client.messaging.message.ClientMessageDispatcher;
import io.github.jotabrc.ovy_mq_client.messaging.payload.handler.interfaces.PayloadConfirmationHandler;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.constants.Mapping;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MessagePayloadConfirmationHandler implements PayloadConfirmationHandler<MessagePayload> {

    private final ClientMessageDispatcher clientMessageDispatcher;

    @Override
    public void acknowledge(SessionManager session,
                            Client client,
                            MessagePayload payload) {
        OvyAction ovyAction = buildAction(payload);
        clientMessageDispatcher.send(client, client.getTopic(), Mapping.SEND_COMMAND_TO_SERVER, ovyAction, session);
        client.setIsMessageInteractionActive(false);
    }

    private OvyAction buildAction(MessagePayload payload) {
        return OvyAction.builder()
                .commands(List.of(OvyCommand.REMOVE_MESSAGE_PAYLOAD))
                .payload(payload.cleanDataAndUpdateSuccessTo(true))
                .build();
    }

    @Override
    public Class<?> supports() {
        return MessagePayload.class;
    }
}
