package io.github.jotabrc.ovy_mq_core.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class LockProcessor {

    private final Map<Long, ReentrantLock> partitionLock = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> topicLock = new ConcurrentHashMap<>();

    public <T> T getReentrantLockAndExecute(Callable<T> callable, Long partition) {
        Objects.requireNonNull(partition, "Partition number");
        ReentrantLock lock = partitionLock.computeIfAbsent(partition, k -> new ReentrantLock(true));
        return executeLockAndCallable(callable, partition.toString(), lock);
    }

    public <T> T getReentrantLockAndExecute(Callable<T> callable, String topic) {
        Objects.requireNonNull(topic, "Topic");
        ReentrantLock lock = topicLock.computeIfAbsent(topic, k -> new ReentrantLock(true));
        return executeLockAndCallable(callable, topic, lock);
    }

    private <T> T executeLockAndCallable(Callable<T> callable, String topic, ReentrantLock lock) {
        lock.lock();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new IllegalStateException("Error executing lock with key=%s: %s".formatted(topic, e.getMessage()), e);
        } finally {
            lock.unlock();
        }
    }
}
