package io.github.jotabrc.ovy_mq.queue.repository.impl;

import io.github.jotabrc.ovy_mq.queue.impl.PartitionManager;
import io.github.jotabrc.ovy_mq.queue.interfaces.FileStorageManager;
import io.github.jotabrc.ovy_mq.queue.repository.interfaces.FileRepository;
import io.github.jotabrc.ovy_mq.queue.repository.interfaces.MessageRepository;
import io.github.jotabrc.ovy_mq.queue.util.FilePath;
import io.github.jotabrc.ovy_mq_core.components.LockProcessor;
import io.github.jotabrc.ovy_mq_core.domain.IndexData;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
@Service
@Primary
public class InFileRepository implements MessageRepository {

    private final FileStorageManager fileStorageManager;
    private final FileRepository fileRepository;
    private final PartitionManager partitionManager;
    private final LockProcessor lockProcessor;

    /*
    TODO:
        memory queue with file
        awaiting confirmation calculation
     */

    @Override
    public MessagePayload saveToQueue(MessagePayload messagePayload) {
        log.info("Saving message={} topic-key={}", messagePayload.getId(), messagePayload.getTopicKey());
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
        return Optional.ofNullable(fileRepository.readIndexByTopicAndGetFirst(topic, partitionManager.getPartitionsFor(FilePath.INDEX_PATH)))
                .map(data -> fileRepository.readQueueAndGet(data, MessagePayload.class, FilePath.QUEUE_PATH.withPartition(data.partitionNumber())))
                .or(Optional::empty);
    }

    @Override
    public List<MessagePayload> getMessagesByLastUsedDateGreaterThen(Long ms) {
        log.info("In file repository - get messages by last used date greater then -> is disabled");
        return Collections.emptyList();
    }

    @Override
    public void removeFromQueue(String topic, String messageId) {
        Optional.ofNullable(fileRepository.readIndexByIdAndGetFirst(messageId, partitionManager.getPartitionsFor(FilePath.INDEX_PATH)))
                .ifPresent(data -> lockProcessor.getReentrantLockAndExecute(() -> fileRepository.writeIndex(data, FilePath.INDEX_REMOVED_PATH.withPartition(data.partitionNumber())), data.partitionNumber()));
    }

    @Override
    public void removeAndRequeue(MessagePayload messagePayload) {
        log.info("In file repository - remove and requeue -> is disabled");
    }

    @Override
    public Integer getAwaitingConfirmationQuantity() {
        return 0;
    }
}
