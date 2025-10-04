package io.github.jotabrc.ovy_mq_client.service.domain.client;

import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.handler.MessageProcessingFailureException;
import io.github.jotabrc.ovy_mq_client.service.domain.client.interfaces.ClientMessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageHandlerImpl implements ClientMessageHandler {

    @Override
    public <T, R> void execute(T t, R r) {
        handleMessage((String) t, (MessagePayload) r);
    }

    @Override
    public void handleMessage(String topic, MessagePayload object) {
        log.info("Invoking consumer for topic: {}", topic);
        try {
            ClientExecutor.CLIENT_METHOD.getHandler().execute(topic, object.getPayload());
            log.info("Consumer received the message for topic: {}", topic);
            // TODO: handle success
        } catch (MessageProcessingFailureException e) {
            // TODO: handle message processing failures
        }
    }
}
