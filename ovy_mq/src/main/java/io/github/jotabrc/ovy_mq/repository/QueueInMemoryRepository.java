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
            log.info("Saving message {} for topic {}", messagePayload.getId(), key);
            queue.offer(messagePayload);
            return queue;
        });
        return messagePayload;
    }

    @Override
    public synchronized MessagePayload removeFromQueueAndReturn(String topic) {
        return messages.get(topic).poll();
    }

    @Override
    public List<MessagePayload> getMessagesByLastUsedDateGreaterThen(Long ms) {
        return messages.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(s -> s.getProcessingStartedAt().minus(ms, ChronoUnit.MILLIS).isAfter(OffsetDateTime.now()))
                .toList();
    }

    @Override
    public synchronized void removeFromProcessingQueue(String topic, String messageId) {
        for (MessagePayload message : messages.get(topic)) {
            if (Objects.equals(messageId, message.getId())) {
                messages.get(topic).remove(message);
                break;
            }
        }
    }
}
