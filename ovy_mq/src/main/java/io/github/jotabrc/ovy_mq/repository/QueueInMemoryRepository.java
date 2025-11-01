package io.github.jotabrc.ovy_mq.repository;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Profile("dev")
@Slf4j
@RequiredArgsConstructor
@Service
public class QueueInMemoryRepository implements MessageRepository{

    private Map<String, Queue<MessagePayload>> messages = new ConcurrentHashMap();

    @Override
    public MessagePayload saveToQueue(MessagePayload messagePayload) {
        messages.compute(messagePayload.getTopic(), (key, queue) -> {
            if (isNull(queue)) queue = new ConcurrentLinkedQueue<>();
            log.info("Saving message={} topic-key={}", messagePayload.getId(), key);
            queue.offer(messagePayload);
            return queue;
        });
        return messagePayload;
    }

    @Override
    public synchronized MessagePayload pollFromQueue(String topic) {
        return messages.get(topic).poll();
    }

    @Override
    public List<MessagePayload> getMessagesByLastUsedDateGreaterThen(Long ms) {
        return messages.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(s -> nonNull(s.getProcessingStartedAt()))
                .filter(s -> s.getProcessingStartedAt().minus(ms, ChronoUnit.MILLIS).isAfter(OffsetDateTime.now()))
                .toList();
    }

    @Override
    public synchronized void removeFromQueue(String topic, String messageId) {
        messages.get(topic).removeIf(m -> Objects.equals(messageId, m.getId()));
    }

    @Override
    public synchronized void removeAndRequeue(MessagePayload messagePayload) {
        removeFromQueue(messagePayload.getTopic(), messagePayload.getId());
        saveToQueue(messagePayload);
    }
}
