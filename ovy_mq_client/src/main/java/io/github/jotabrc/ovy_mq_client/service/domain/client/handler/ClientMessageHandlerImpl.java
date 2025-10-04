package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.Action;
import io.github.jotabrc.ovy_mq_client.domain.Command;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.handler.MessageProcessingFailureException;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageHandlerImpl implements ClientMessageHandler {

    @Override
    public void execute(Action action) {
        if (Objects.equals(Command.EXECUTE_CLIENT_MESSAGE_HANDLER_HANDLE_MESSAGE, action.getCommand())) {
            handleMessage(action.getClient().getTopic(), action.getMessagePayload());
        }
    }

    @Override
    public void handleMessage(String topic, MessagePayload object) {
        log.info("Invoking consumer for topic: {}", topic);
        try {
            ClientHandler.CLIENT_METHOD.getHandler().execute(topic, object.getPayload());
            log.info("Consumer received the message for topic: {}", topic);
        } catch (MessageProcessingFailureException e) {
            // TODO: handle message processing failures
        }
    }
}
