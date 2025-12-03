package io.github.jotabrc.ovy_mq_core.components;

import io.github.jotabrc.ovy_mq_core.domain.concurrency.ThreadLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class LockProcessor {

    private final Map<String, ThreadLock> locks = new ConcurrentHashMap<>();

    public ThreadLock getLockByTopic(String topic) {
        return getLockByTopicAndMessageId(topic, null);
    }

    public ThreadLock getLockByClientId(String clientId) {
        return getLock(null, null, clientId);
    }

    public ThreadLock getLockByTopicAndClientId(String topic, String clientId) {
        return getLock(topic, null, clientId);
    }

    public ThreadLock getLockByTopicAndMessageId(String topic, String messageId) {
        return getLock(topic, messageId, null);
    }

    public ThreadLock getLock(String topic, String messageId, String clientId) {
        String key = getKey(topic, messageId, clientId);
        return locks.computeIfAbsent(key, k -> ThreadLock.builder()
                .topic(topic)
                .messageId(messageId)
                .clientId(clientId)
                .build());
    }

    public <T> T getLockAndExecute(Callable<T> callable, String topic, String messageId, String clientId) {
        ThreadLock lock = getLock(topic, messageId, clientId);
        log.info("Thread={} requesting lock={}", Thread.currentThread().getName(), getKey(topic, messageId, clientId));
        synchronized (lock) {
            log.info("Thread={} acquired lock={}: processing request", Thread.currentThread().getName(), getKey(topic, messageId, clientId));
            try {
                return callable.call();
            } catch (Exception e) {
                throw new IllegalStateException("Error executing lock with key=%s: %s"
                        .formatted(getKey(topic, messageId, clientId), e.getMessage()));
            } finally {
                this.removeLock(lock);
            }
        }
    }

    public void removeLock(ThreadLock threadLock) {
        locks.remove(getKey(threadLock.getTopic(), threadLock.getMessageId(), threadLock.getClientId()));
    }

    private String getKey(String topic, String messageId, String clientId) {
        return "key:"
                .concat(getOrDefault(topic))
                .concat(":")
                .concat(getOrDefault(messageId))
                .concat(":")
                .concat(getOrDefault(clientId));
    }

    private String getOrDefault(String str) {
        return isNull(str)
                ? "null"
                : str;
    }
}
