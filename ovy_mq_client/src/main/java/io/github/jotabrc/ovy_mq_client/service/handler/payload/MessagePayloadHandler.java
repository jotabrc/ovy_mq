package io.github.jotabrc.ovy_mq_client.service.handler.payload;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.service.handler.payload.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.interfaces.ClientRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessagePayloadHandler implements PayloadHandler<MessagePayload> {

    private final ClientRegistry clientRegistry;

    private static final Pattern PATTERN_EXTRACT_TOPIC = Pattern.compile("/user/queue/(.*)");

    @Async
    @Override
    public void handle(String clientId, MessagePayload payload, StompHeaders headers) {
        String destination = headers.getDestination();
        if (nonNull(destination)) {
            String topic = extractTopicFromDestination(destination);
            if (nonNull(topic)) {
                payload.setTopic(topic);
                handle(clientId, payload);
            }
        }
    }

    private String extractTopicFromDestination(String destination) {
        Matcher matcher = PATTERN_EXTRACT_TOPIC.matcher(destination);
        if (matcher.find()) return matcher.group(1);
        return null;
    }

    private void handle(String clientId, MessagePayload messagePayload) {
        log.info("Retrieving client={} topic={}", clientId, messagePayload.getTopic());
        Client client = clientRegistry.getByClientIdOrThrow(clientId);
        client.confirmPayloadReceived(messagePayload);
        try {
            client.setIsAvailable(false);
            client.getMethod().invoke(client.getBeanInstance(), messagePayload.getPayload());
            log.info("Client consuming message={} topic={}", messagePayload.getId(), messagePayload.getTopic());
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.warn("Error while processing payload={} topic={}", messagePayload.getId(), messagePayload.getTopic(), e);
            throw new RuntimeException(e);
        } finally {
            client.setIsAvailable(true);
            client.requestMessage();
        }
    }

    @Override
    public Class<MessagePayload> supports() {
        return MessagePayload.class;
    }
}
