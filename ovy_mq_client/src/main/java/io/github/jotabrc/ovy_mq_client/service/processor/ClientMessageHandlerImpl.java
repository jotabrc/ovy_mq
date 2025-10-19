package io.github.jotabrc.ovy_mq_client.service.processor;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.service.processor.interfaces.ClientMessageHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageHandlerImpl implements ClientMessageHandler {

    private final ClientRegistry clientRegistry;

    @Async
    @Override
    public void handle(String clientId, String topic, MessagePayload messagePayload) {
        log.info("Retrieving client={} for topic={}", clientId, topic);
        Client client = clientRegistry.getByClientIdOrThrow(clientId);
        try {
            client.setIsAvailable(false);
            client.getMethod().invoke(client.getBeanInstance(), messagePayload.getPayload());
            messagePayload.cleanDataAndUpdateSuccessValue(true);
            log.info("Client consuming message={} for topic={}", messagePayload.getId(), topic);
        } catch (Exception e) {
            log.info("Error processing message={}", messagePayload.getId());
        } finally {
            client.setIsAvailable(true);
            client.confirmProcessing(messagePayload);
            client.requestMessage();
        }
    }
}
