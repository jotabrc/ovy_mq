package io.github.jotabrc.ovy_mq_client.component.payload;

import io.github.jotabrc.ovy_mq_client.component.listener.ListenerExecutionContextHolder;
import io.github.jotabrc.ovy_mq_client.component.listener.ListenerInvocator;
import io.github.jotabrc.ovy_mq_client.component.payload.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import io.github.jotabrc.ovy_mq_core.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessagePayloadHandler implements PayloadHandler<MessagePayload> {

    private final Executor listenerExecutor;
    private final ListenerExecutionContextHolder listenerExecutionContextHolder;
    private final ListenerInvocator listenerInvocator;

    @Value("${ovymq.client.processing.timeout:150000}")
    private Long timeout;

    private static final Pattern PATTERN_EXTRACT_TOPIC = Pattern.compile("/user/queue/(.*)");

    @Override
    public void handle(Client client, MessagePayload payload, StompHeaders headers) {
        extractTopicFrom(headers.getDestination()).ifPresent(topic -> {
            payload.setTopic(topic);
            handleAsync(client, payload);
        });
    }

    private Optional<String> extractTopicFrom(String destination) {
        Matcher matcher = PATTERN_EXTRACT_TOPIC.matcher(destination);
        if (matcher.find()) return Optional.of(matcher.group(1));
        return Optional.empty();
    }

    private void handleAsync(Client client, MessagePayload messagePayload) {
        CompletableFuture.runAsync(() -> execute(messagePayload, client), listenerExecutor)
                .orTimeout(timeout, TimeUnit.MILLISECONDS)
                .exceptionally(e -> {
                    log.error("Listener task failed: client={} message={} topic={}", client.getId(), messagePayload.getId(), messagePayload.getTopic(), e);
                    return null;
                });
    }

    private void execute(MessagePayload messagePayload, Client client) {
        try {
            client.setIsAvailable(false);
            listenerExecutionContextHolder.setThreadLocal(client);
            log.info("Executing client={}: message={} topic={} class={} method={}", client.getId(), messagePayload.getId(), messagePayload.getTopic(), client.getBeanName(), client.getMethod().getName());
            listenerInvocator.invoke(client, messagePayload.getPayload());
        } catch (Throwable e) {
            log.warn("Error while executing client={}: message={} topic={} class={} method={}", client.getId(), messagePayload.getId(), messagePayload.getTopic(), client.getBeanName(), client.getMethod().getName(), e);
            client.setIsAvailable(true);
            throw new OvyException.ListenerInvocationExecution("Error while executing client=%s: message=%s topic=%s class=%s method=%s".formatted(client.getId(), messagePayload.getId(), messagePayload.getTopic(), client.getBeanName(), client.getMethod().getName()));
        }
    }

    @Override
    public Class<MessagePayload> supports() {
        return MessagePayload.class;
    }
}
