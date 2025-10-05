package io.github.jotabrc.ovy_mq.repository;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
    public void saveToQueue(MessagePayload message) {
        messages.compute(message.getTopicKey(), (key, queue) -> {
            if (isNull(queue)) queue = new ConcurrentLinkedQueue<>();
            queue.offer(message);
            return queue;
        });
    }

    @Override
    public MessagePayload removeFromQueueAndReturn(String topic) {
        return messages.get(topic).poll();
    }

    @Override
    public List<MessagePayload> removeFromQueueAndReturnList(String topic, int quantity) {
        Queue<MessagePayload> queue = messages.get(topic);
        List<MessagePayload> returningMessages = new ArrayList<>();
        while (returningMessages.size() < quantity) {
            returningMessages.add(queue.poll());
        }

        return returningMessages;
    }

    @Override
    public void removeFromProcessingQueue(String topic) {
        messages.get(topic).poll();
    }
}
