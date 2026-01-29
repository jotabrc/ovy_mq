package io.github.jotabrc.ovy_mq_client.producer;

import io.github.jotabrc.ovy_mq_client.producer.interfaces.OvyProducer;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.stomp_handler.StompClientSessionHandler;
import io.github.jotabrc.ovy_mq_core.constants.Mapping;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.List;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class StompOvyProducer implements OvyProducer<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> {

    private final ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler> clientAdapter;

    //    @Async(ThreadPoolConfig.PRODUCER_EXECUTOR) todo
    @Override
    public void send(String topic, Object payload) {
        if (isNull(payload))
            throw new IllegalArgumentException("Payload required to send message");
        OvyAction ovyAction = buildAction(topic, payload);
        clientAdapter.getClientMessageSender().send(Mapping.SEND_COMMAND_TO_SERVER, ovyAction);
    }

    private OvyAction buildAction(String topic, Object payload) {
        return OvyAction.builder()
                .commands(List.of(OvyCommand.SAVE_MESSAGE_PAYLOAD))
                .payload(MessagePayload.builder()
                        .topic(topic)
                        .payload(payload)
                        .build())
                .build();
    }
}
