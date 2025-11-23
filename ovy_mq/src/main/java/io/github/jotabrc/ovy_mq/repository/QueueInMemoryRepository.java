package io.github.jotabrc.ovy_mq.repository;

import io.github.jotabrc.ovy_mq_core.components.LockProcessor;
import io.github.jotabrc.ovy_mq_core.domain.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.nonNull;

@Profile("dev")
@Slf4j
@RequiredArgsConstructor
@Service
public class QueueInMemoryRepository implements MessageRepository {

    private final Map<String, Queue<MessagePayload>> messages = new ConcurrentHashMap<>();

    private final LockProcessor lockProcessor;

    @Override
    public MessagePayload saveToQueue(MessagePayload messagePayload) {
        log.info("Saving message={} topic-key={}", messagePayload.getId(), messagePayload.getTopic());
        messages.computeIfAbsent(messagePayload.getTopic(), k -> new ConcurrentLinkedQueue<>()).offer(messagePayload);
        return messagePayload;
    }

    @Override
    public MessagePayload pollFromQueue(String topic) {
        synchronized (lockProcessor.getLockByTopic(topic)) {
            return messages.get(topic).poll();
        }
    }

    @Override
    public List<MessagePayload> getMessagesByLastUsedDateGreaterThen(Long ms) {
        return messages.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(s -> nonNull(s.getProcessingStartedAt()))
                .filter(s -> ChronoUnit.MILLIS.between(s.getProcessingStartedAt(), OffsetDateTime.now()) > ms)
                .toList();
    }

    @Override
    public void removeFromQueue(String topic, String messageId) {
        synchronized (lockProcessor.getLockByTopicAndMessageId(topic, messageId)) {
            messages.get(topic).removeIf(m -> Objects.equals(messageId, m.getId()));
        }
    }

    @Override
    public void removeAndRequeue(MessagePayload messagePayload) {
        synchronized (lockProcessor.getLockByTopicAndMessageId(messagePayload.getTopic(), messagePayload.getId())) {
            removeFromQueue(messagePayload.getTopic(), messagePayload.getId());
            saveToQueue(messagePayload);
        }
    }
}
