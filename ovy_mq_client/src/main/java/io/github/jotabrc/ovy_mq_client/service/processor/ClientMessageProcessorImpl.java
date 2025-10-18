package io.github.jotabrc.ovy_mq_client.service.processor;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
import io.github.jotabrc.ovy_mq_client.service.processor.interfaces.ClientMessageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageProcessorImpl implements ClientMessageProcessor {

    private final ClientRegistry clientRegistry;
    private final ApplicationContext applicationContext;

    @Async
    @Override
    public void process(String clientId, String topic, MessagePayload messagePayload) {
        log.info("Retrieving client={} for topic={}", clientId, topic);
        Client client = clientRegistry.getByClientIdOrThrow(clientId);
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
            client.requestMessage();
        }
    }
}
