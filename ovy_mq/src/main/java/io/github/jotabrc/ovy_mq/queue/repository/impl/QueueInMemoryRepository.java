package io.github.jotabrc.ovy_mq.queue.repository.impl;

import io.github.jotabrc.ovy_mq.queue.repository.interfaces.MessageRepository;
import io.github.jotabrc.ovy_mq_core.components.LockProcessor;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueueInMemoryRepository implements MessageRepository {

    private final Map<String, Queue<MessagePayload>> messages = new ConcurrentHashMap<>();
    private final AtomicInteger awaitingConfirmation = new AtomicInteger(0);

    private final LockProcessor lockProcessor;

    @Override
    public MessagePayload saveToQueue(MessagePayload messagePayload) {
        log.info("Saving message={} topic-key={}", messagePayload.getId(), messagePayload.getTopicKey());
        messages.computeIfAbsent(messagePayload.getTopicKey(), k -> new ConcurrentLinkedQueue<>()).offer(messagePayload);
        return messagePayload;
    }

    @Override
    public Optional<MessagePayload> pollFromQueue(String topic) {
        Callable<Optional<MessagePayload>> callable = () -> {
            if (!messages.isEmpty()) {
                Optional<MessagePayload> payload = Optional.ofNullable(messages.getOrDefault(topic, new ConcurrentLinkedQueue<>()).poll());
                if (payload.isPresent()) {
                    awaitingConfirmation.incrementAndGet();
                }
                return payload;
            } else return Optional.empty();
        };
        return lockProcessor.getLockAndExecute(callable, topic, null, null);
    }

    @Override
    public List<MessagePayload> getMessagesByLastUsedDateGreaterThen(Long ms) {
        Callable<List<MessagePayload>> callable = () -> {
            return messages.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(s -> nonNull(s.getProcessingStartedAt()))
                    .filter(s -> ChronoUnit.MILLIS.between(s.getProcessingStartedAt(), OffsetDateTime.now()) > ms)
                    .toList();
        };
        return lockProcessor.getLockAndExecute(callable, null, null, null);
    }

    @Override
    public void removeFromQueue(String topic, String messageId) {
        Callable<Void> callable = () -> {
            if (!messages.isEmpty()) {
                messages.get(topic).removeIf(m -> Objects.equals(messageId, m.getId()));
                awaitingConfirmation.decrementAndGet();
            }
            return null;
        };
        lockProcessor.getLockAndExecute(callable, topic, messageId, null);
    }

    @Override
    public void removeAndRequeue(MessagePayload messagePayload) {
        Callable<Void> callable = () -> {
            removeFromQueue(messagePayload.getTopicKey(), messagePayload.getId());
            saveToQueue(messagePayload);
            return null;
        };
        lockProcessor.getLockAndExecute(callable, messagePayload.getTopicKey(), messagePayload.getId(), null);
    }

    @Override
    public Integer getAwaitingConfirmationQuantity() {
        return awaitingConfirmation.get();
    }
}
