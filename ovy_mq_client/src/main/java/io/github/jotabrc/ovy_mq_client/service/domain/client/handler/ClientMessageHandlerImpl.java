package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.*;
import io.github.jotabrc.ovy_mq_client.domain.factory.ClientFactory;
import io.github.jotabrc.ovy_mq_client.domain.factory.HandlerActionFactory;
import io.github.jotabrc.ovy_mq_client.handler.MessageProcessingFailureException;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static io.github.jotabrc.ovy_mq_client.service.domain.client.handler.ClientCommand.SEND_MESSAGE_TO_CONSUMER;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageHandlerImpl implements ClientMessageHandler {

    @Override
    public void handleMessage(String topic, MessagePayload messagePayload) {
        log.info("Invoking consumer for topic: {}", topic);
        try {
            HandlerActionFactory.of(ClientFactory.createConsumer(topic), messagePayload).execute(SEND_MESSAGE_TO_CONSUMER);
            log.info("Consumer received the message for topic: {}", topic);
        } catch (MessageProcessingFailureException e) {
            // TODO: handle message processing failures
        }
    }
}
