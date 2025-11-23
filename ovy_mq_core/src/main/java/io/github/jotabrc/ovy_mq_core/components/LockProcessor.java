package io.github.jotabrc.ovy_mq_core.components;

import io.github.jotabrc.ovy_mq_core.domain.ThreadLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
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
        ThreadLock lock = locks.get(key);
        if (isNull(lock)) {
            lock = new ThreadLock(topic, messageId, clientId);
            locks.put(key, lock);
        }
        log.info("Thread={} requesting lock={}", Thread.currentThread().getName(), lock);
        return lock;
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
