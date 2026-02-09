package io.github.jotabrc.ovy_mq_core.components;

import io.github.jotabrc.ovy_mq_core.domain.concurrency.ThreadLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class LockProcessor {

    private final Map<String, ThreadLock> locks = new ConcurrentHashMap<>();
    private final Map<Long, ReentrantLock> partitionLock = new ConcurrentHashMap<>();

    public <T> T getLockAndExecute(Callable<T> callable, String topic, String messageId, String clientId) {
        ThreadLock lock = getLock(topic, messageId, clientId);
        return acquireLockAndExecute(callable, getKey(topic, messageId, clientId), lock);
    }

    public <T> T getLockAndExecute(Callable<T> callable, String key) {
        ThreadLock lock = getLock(key);
        return acquireLockAndExecute(callable, key, lock);
    }

    public <T> T getReentrantLockAndExecute(Callable<T> callable, Long partition) {
        Objects.requireNonNull(partition, "Partition number");
        ReentrantLock lock = partitionLock.computeIfAbsent(partition, k -> new ReentrantLock(true));
        lock.lock();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new IllegalStateException("Error executing lock with partition-key=%s: %s".formatted(partition, e.getMessage()), e);
        } finally {
            lock.unlock();
        }
    }

    private <T> T acquireLockAndExecute(Callable<T> callable, String key, ThreadLock lock) {
        Objects.requireNonNull(lock, "ThreadLock");
        log.info("Thread={} requesting lock={}", Thread.currentThread().getName(), key);
        synchronized (lock) {
            log.info("Thread={} acquired lock={}: processing request", Thread.currentThread().getName(), key);
            try {
                return callable.call();
            } catch (Exception e) {
                throw new IllegalStateException("Error executing lock with key=%s: %s"
                        .formatted(key, e.getMessage()));
            } finally {
                this.removeLock(lock);
            }
        }
    }

    private ThreadLock getLock(String topic, String messageId, String clientId) {
        String key = getKey(topic, messageId, clientId);
        return locks.computeIfAbsent(key, k -> ThreadLock.builder()
                .key(key)
                .build());
    }

    private ThreadLock getLock(String key) {
        return locks.computeIfAbsent(key, k -> ThreadLock.builder()
                .key(key)
                .build());
    }

    private void removeLock(ThreadLock threadLock) {
        locks.remove(threadLock.getKey());
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
