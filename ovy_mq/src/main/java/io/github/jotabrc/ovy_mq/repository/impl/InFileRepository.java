package io.github.jotabrc.ovy_mq.repository.impl;

import io.github.jotabrc.ovy_mq.repository.interfaces.FileRepository;
import io.github.jotabrc.ovy_mq.repository.interfaces.FileStorageManager;
import io.github.jotabrc.ovy_mq.repository.interfaces.MessageRepository;
import io.github.jotabrc.ovy_mq_core.domain.IndexData;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Primary
public class InFileRepository implements MessageRepository {

    private final FileStorageManager fileStorageManager;
    private final FileRepository fileRepository;
    private final MessageRepository queueInMemoryRepository;

    @Override
    public MessagePayload saveToQueue(MessagePayload messagePayload) {
        log.info("Saving message={} topic-key={}", messagePayload.getId(), messagePayload.getTopicKey());
        return Optional.ofNullable(fileRepository.writeQueue(messagePayload, PathType.QUEUE_PATH.getPath()))
                .map(data -> {
                    data = new IndexData(messagePayload.getId(), data.size(), fileStorageManager.getOffset(PathType.QUEUE_PATH.getPath()) + data.size(), messagePayload.getTopic(), data.storedAt());
                    data = fileRepository.writeIndex(data, PathType.INDEX_PATH.getPath());
                    fileStorageManager.updateOffset(data.size().longValue());
                    return queueInMemoryRepository.saveToQueue(messagePayload);
                })
                .orElse(null);
    }

    @Override
    public Optional<MessagePayload> pollFromQueue(String topic) {
        return queueInMemoryRepository.pollFromQueue(topic)
                .or(() -> Optional.ofNullable(fileRepository.readIndexByTopicAndGetFirst(topic, PathType.INDEX_PATH.getPath()))
                        .map(data -> fileRepository.readQueueAndGet(data, MessagePayload.class, PathType.QUEUE_PATH.getPath()))
                        .or(Optional::empty));
    }

    @Override
    public List<MessagePayload> getMessagesByLastUsedDateGreaterThen(Long ms) {
        return queueInMemoryRepository.getMessagesByLastUsedDateGreaterThen(ms);
    }

    @Override
    public void removeFromQueue(String topic, String messageId) {
        queueInMemoryRepository.removeFromQueue(topic, messageId);
        Optional.ofNullable(fileRepository.readIndexByIdAndGetFirst(messageId, PathType.INDEX_PATH.getPath()))
                .ifPresent(data -> fileRepository.writeIndex(data, PathType.INDEX_REMOVED_PATH.getPath()));
    }

    @Override
    public void removeAndRequeue(MessagePayload messagePayload) {
        queueInMemoryRepository.removeAndRequeue(messagePayload);
    }

    @Override
    public Integer getAwaitingConfirmationQuantity() {
        return queueInMemoryRepository.getAwaitingConfirmationQuantity();
    }
}
