package io.github.jotabrc.ovy_mq.queue.impl;

import io.github.jotabrc.ovy_mq.queue.interfaces.FileStorageManager;
import io.github.jotabrc.ovy_mq.queue.repository.interfaces.FileRepository;
import io.github.jotabrc.ovy_mq.queue.util.FilePath;
import io.github.jotabrc.ovy_mq.queue.util.FilePathHelper;
import io.github.jotabrc.ovy_mq_core.domain.IndexData;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class FileStorageManagerImpl implements FileStorageManager {

    private final FileRepository fileRepository;
    private final FilePathHelper filePathHelper;
    private final PartitionManager partitionManager;
    private final Map<Long, Long> offsets = new HashMap<>();

    /*
    TODO
        1: File partitions
        2: ReentrantLock
     */

    @PostConstruct
    private void init() {
        initialize();
    }

    @Override
    public void initialize() {
        partitionManager.getPartitionsFor(FilePath.INDEX_PATH)
                .stream()
                .peek(path -> offsets.put(filePathHelper.extractPartition(path), 0L))
                .forEach(this::rebuild);
    }

    @Override
    public void rebuild(String filePath) {
        long partition = filePathHelper.extractPartition(filePath);

        String indexPath = filePathHelper.createPath(FilePath.INDEX_PATH, partition);
        String indexTempPath = filePathHelper.createPath(FilePath.INDEX_PATH_TMP, partition);
        String queuePath = filePathHelper.createPath(FilePath.QUEUE_PATH, partition);
        String queueTempPath = filePathHelper.createPath(FilePath.QUEUE_PATH_TMP, partition);
        String indexRemovedPath = filePathHelper.createPath(FilePath.INDEX_REMOVED_PATH, partition);

        fileRepository.createFile(indexPath);
        fileRepository.createFile(queuePath);
        fileRepository.createFile(indexRemovedPath);
        Path tempIndex = fileRepository.createFile(indexTempPath);
        Path tempQueue = fileRepository.createFile(queueTempPath);

        AtomicLong newOffset = new AtomicLong(0);

        /*
        TODO:
            rebuild performance
         */

        Set<String> idsToRemove = new HashSet<>();
        try (BufferedReader reader = fileRepository.getIndexReader(indexRemovedPath)) {
             idsToRemove.addAll(fileRepository.readIndexAndGetAllIds(reader, indexRemovedPath));
        } catch (IOException e) {
            throw new OvyException.ReadOperation("Error while reading file", e.getMessage(), indexPath);
        }

        try (BufferedReader indexReader = fileRepository.getIndexReader(indexPath)) {
            while (true) {
                boolean checkMessagesRemoved = false;
                IndexData data = fileRepository.readIndexNextLine(indexReader, indexPath, checkMessagesRemoved);
                if (isNull(data)) break;
                else if (idsToRemove.contains(data.id())) continue;

                MessagePayload payload = fileRepository.readQueueAndGet(data, MessagePayload.class, queuePath);
                if (isNull(payload)) continue;

                Optional.ofNullable(fileRepository.writeQueue(payload, queueTempPath))
                        .ifPresent(writtenData -> {
                            writtenData = new IndexData(payload.getId(), writtenData.size(), newOffset.get(), payload.getTopic(), writtenData.storedAt(), data.partitionNumber());
                            fileRepository.writeIndex(writtenData, indexTempPath);
                            newOffset.getAndAdd(writtenData.size());
                        });
            }
        } catch (IOException e) {
            throw new OvyException.ReadOperation("Error while reading file", e.getMessage(), indexPath);
        }

        try {
            Files.move(tempIndex, fileRepository.filePath(indexPath), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new OvyException.ReplaceOperation("Error while replacing file in rebuild operation", e.getMessage(), tempIndex.toString());
        }

        try {
            Files.move(tempQueue, fileRepository.filePath(queuePath), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new OvyException.ReplaceOperation("Error while replacing file in rebuild operation", e.getMessage(), tempQueue.toString());
        }

        this.offsets.put(partition, newOffset.get());

        cleanRemovedIndex(indexRemovedPath);
    }

    private static void cleanRemovedIndex(String indexRemovedPathName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexRemovedPathName, false))) {
            writer.write("");
        } catch (IOException e) {
            throw new OvyException.WriteOperation("Error while writing to file", e.getMessage(), indexRemovedPathName);
        }
    }

    @Override
    public Long updateOffset(Long offset, Long partition) {
        return this.offsets.put(partition, offset);
    }

    @Override
    public Long getOffset(String filePath) {
        return fileRepository.fileOffset(filePath);
    }

    @Override
    public Long getOffset(Long partition) {
        return this.offsets.get(partition);
    }
}
