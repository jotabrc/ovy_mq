package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientMessageHandler;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientRegistryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageHandlerImpl implements ClientMessageHandler {

    private final ClientRegistryHandler clientRegistryHandler;
    private final ApplicationContext applicationContext;

    @Async
    @Override
    public void handleMessage(String clientId, String topic, MessagePayload messagePayload) {
        log.info("Retrieving client={} for topic={}", clientId, topic);
        Client client = clientRegistryHandler.getByClientIdOrThrow(clientId);
        try {
            client.setIsAvailable(false);
            Object proxy = applicationContext.getBean(client.getMethod().getDeclaringClass());
            client.getMethod().invoke(proxy, messagePayload.getPayload());
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
