package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.handler.MessageProcessingFailureException;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMessageHandler;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientRegistryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageHandlerImpl implements ClientMessageHandler {

    private final ClientRegistryHandler clientRegistryHandler;

    @Override
    public void handleMessage(String topic, MessagePayload messagePayload) {
        log.info("Invoking consumer for topic: {}", topic);
        try {
            clientRegistryHandler.executeListener(topic, messagePayload);
            log.info("Consumer received the message for topic: {}", topic);
        } catch (MessageProcessingFailureException e) {
            // TODO: handle message processing failures
        }
    }
}
