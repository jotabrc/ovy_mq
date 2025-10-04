package io.github.jotabrc.ovy_mq.repository;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("dev")
@Slf4j
@RequiredArgsConstructor
@Service
public class QueueInMemoryRepository implements MessageRepository{
//TODO: in memory queue for testing
    @Override
    public void saveToQueue(MessagePayload message) {

    }

    @Override
    public MessagePayload removeFromQueueAndReturn(String topic) {
        return null;
    }

    @Override
    public List<MessagePayload> removeFromQueueAndReturnList(String topic, int quantity) {
        return List.of();
    }

    @Override
    public void removeFromProcessingQueue(String topic) {

    }
}
