package io.github.jotabrc.ovy_mq.queue.repository.impl;

import io.github.jotabrc.ovy_mq.queue.impl.PartitionManager;
import io.github.jotabrc.ovy_mq.queue.interfaces.FileStorageManager;
import io.github.jotabrc.ovy_mq.queue.repository.interfaces.FileRepository;
import io.github.jotabrc.ovy_mq.queue.repository.interfaces.MessageRepository;
import io.github.jotabrc.ovy_mq.queue.util.FilePath;
import io.github.jotabrc.ovy_mq_core.components.LockProcessor;
import io.github.jotabrc.ovy_mq_core.domain.IndexData;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
@Service
@Primary
public class InFileRepository implements MessageRepository {

    @Value("${ovymq.in-memory-queue.cache-size:50}")
    private Integer cacheSize;

    private final MessageRepository queueInMemoryRepository;
    private final FileStorageManager fileStorageManager;
    private final FileRepository fileRepository;
    private final PartitionManager partitionManager;
    private final LockProcessor lockProcessor;

    @Override
    public MessagePayload saveToQueue(MessagePayload messagePayload) {
        log.info("Saving message={} topic={}", messagePayload.getId(), messagePayload.getTopic());
        long partitionToUse = partitionManager.getPartitionToUse();
        Callable<MessagePayload> callable = () -> {
            long currentOffset = fileStorageManager.getOffset(partitionToUse);
            String pathWithPartition = FilePath.QUEUE_PATH.withPartition(partitionToUse);
            return Optional.ofNullable(fileRepository.writeQueue(messagePayload, pathWithPartition))
                    .map(data -> {
                        data = new IndexData(messagePayload.getId(), data.size(), currentOffset, messagePayload.getTopic(), data.storedAt(), data.partitionNumber());
                        data = fileRepository.writeIndex(data, FilePath.INDEX_PATH.withPartition(data.partitionNumber()));
                        fileStorageManager.updateOffset(currentOffset + data.size(), partitionToUse);
                        return messagePayload;
                    })
                    .orElse(null);
        };
        return lockProcessor.getReentrantLockAndExecute(callable, partitionToUse);
    }

    @Override
    public Optional<MessagePayload> pollFromQueue(String topic) {
        Optional<MessagePayload> messagePayload = queueInMemoryRepository.pollFromQueue(topic);
        if (messagePayload.isPresent()) {
            return messagePayload
                    .map(payload -> {
                        payload.setMessageStatus(MessageStatus.SENT);
                        return queueInMemoryRepository.saveToQueue(payload);
                    });
        }

        // todo: fix required, always caching
        log.info("Caching new messages for topic={}", topic);
        var messages = Optional.ofNullable(
                        fileRepository.readIndexByTopicAndGetAsMany(topic, partitionManager.getPartitionsInRandomOrderFor(FilePath.INDEX_PATH), cacheSize))
                .stream().flatMap(Set::stream)
                .map(data -> fileRepository.readQueueAndGet(data, MessagePayload.class, FilePath.QUEUE_PATH.withPartition(data.partitionNumber())))
                .toList();

        for (int i = 1; i < messages.size(); i++) {
            queueInMemoryRepository.saveToQueue(messages.get(i));
        }

        return Optional.ofNullable(messages.getFirst())
                .map(toSend -> {
                    toSend.setMessageStatus(MessageStatus.SENT);
                    return queueInMemoryRepository.saveToQueue(toSend);
                });
    }

    @Override
    public List<MessagePayload> getMessagesByLastUsedDateGreaterThen(Long ms) {
        return queueInMemoryRepository.getMessagesByLastUsedDateGreaterThen(ms);
    }

    @Override
    public void removeFromQueue(String topic, String messageId) {
        Optional.ofNullable(fileRepository.readIndexByIdAndGetFirst(messageId, partitionManager.getPartitionsInRandomOrderFor(FilePath.INDEX_PATH)))
                .ifPresent(data -> lockProcessor.getReentrantLockAndExecute(() -> fileRepository.writeIndex(data, FilePath.INDEX_REMOVED_PATH.withPartition(data.partitionNumber())), data.partitionNumber()));
        queueInMemoryRepository.removeFromQueue(topic, messageId);
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
