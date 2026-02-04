package io.github.jotabrc.ovy_mq.repository.impl;

import io.github.jotabrc.ovy_mq.repository.interfaces.FileRepository;
import io.github.jotabrc.ovy_mq.repository.interfaces.FileStorageManager;
import io.github.jotabrc.ovy_mq_core.domain.IndexData;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Service
public class FileStorageManagerImpl implements FileStorageManager {

    public static final String PARTITION_PREFIX_DELIMITER = "-";
    public static final String PARTITION_SUFIX_DELIMITER = "_";

    public static final String FILE_STORAGE_DIRECTORY = "/filestorage";

    public static final String QUEUE_DIRECTORY = "/queue";
    public static final String QUEUE_FILE_NAME = "queue";
    public static final String QUEUE_FILE_EXTENSION = ".bin";

    public static final String INDEX_DIRECTORY = "/index";
    public static final String INDEX_FILE_NAME = "index";
    public static final String INDEX_REMOVED_FILE_NAME = "indexremoved";
    public static final String INDEX_FILE_EXTENSION = ".txt";

    public static final String TEMP_FILE_EXTENSION = ".tmp";

    private final FileRepository fileRepository;
    private Long currentOffset;

    @PostConstruct
    private void init() {
//        rebuild();
    }

    @Override
    public Path filePath(String filePath) {
        return Paths.get(filePath);
    }

    @Override
    public Path directoryPath(Path filePath) {
        return filePath.getParent();
    }

    @Override
    public Path createFile(String filePath) {
        return createFile(filePath(filePath));
    }

    @Override
    public Path createFile(Path filePath) {
        Path directoryPath = directoryPath(filePath);
        if (nonNull(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return filePath;
    }

    @Override
    public Long fileOffset(String filePath) {
        return fileOffset(filePath(filePath));
    }

    @Override
    public Long fileOffset(Path filePath) {
        try {
            return Files.size(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long initialize(String filePath, boolean getOffset) {
        createFile(filePath);
        if (getOffset) {
            return fileOffset(filePath);
        }
        return null;
    }

    @Override
    public void rebuild(String filePath) {
        long partition = extractPartition(filePath);

        String indexPath = createIndexPathName(partition, INDEX_FILE_EXTENSION);
        String indexTempPath = createIndexPathName(partition, TEMP_FILE_EXTENSION);
        String queuePath = createQueuePathName(partition, QUEUE_FILE_EXTENSION);
        String queueTempPath = createQueuePathName(partition, TEMP_FILE_EXTENSION);
        String indexRemovedPathName = createIndexRemovedPathName(partition, INDEX_FILE_EXTENSION);

        Set<String> removedIds = fileRepository.readIndexAndGetAllIds(fileRepository.getIndexReader(indexRemovedPathName));

        Path tempIndex = createFile(indexTempPath);
        Path tempQueue = createFile(queueTempPath);
        AtomicLong newOffset = new AtomicLong(0);

        try (BufferedReader indexReader = fileRepository.getIndexReader(indexPath)) {
            while (true) {
                IndexData data = fileRepository.readIndexNextLine(indexReader);
                if (isNull(data)) break;

                if (removedIds.contains(data.id())) continue;

                MessagePayload payload = fileRepository.readQueueAndGet(data, MessagePayload.class, queuePath);
                if (isNull(payload)) continue;

                Optional.ofNullable(fileRepository.writeQueue(payload, queueTempPath))
                        .ifPresent(writtenData -> {
                            newOffset.getAndAdd(writtenData.size());
                            writtenData = new IndexData(payload.getId(), writtenData.size(), newOffset.get(), payload.getTopic(), writtenData.storedAt());
                            fileRepository.writeIndex(writtenData, indexTempPath);
                        });
            }
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.move(tempIndex, filePath(indexPath), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            Files.move(tempQueue, filePath(queuePath), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.currentOffset = newOffset.get();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexRemovedPathName, false))) {
            writer.write("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long extractPartition(String filePath) {
        return Long.parseLong(filePath.substring(filePath.lastIndexOf(PARTITION_PREFIX_DELIMITER) + PARTITION_PREFIX_DELIMITER.length(),
                filePath.lastIndexOf(PARTITION_SUFIX_DELIMITER)));
    }

    private String createQueuePathName(long partition, String extension) {
        return FILE_STORAGE_DIRECTORY +
                QUEUE_DIRECTORY +
                "/" +
                QUEUE_FILE_NAME +
                PARTITION_PREFIX_DELIMITER +
                partition +
                PARTITION_SUFIX_DELIMITER +
                extension;
    }

    private String createIndexPathName(long partition, String extension) {
        return FILE_STORAGE_DIRECTORY +
                INDEX_DIRECTORY +
                "/" +
                INDEX_FILE_NAME +
                PARTITION_PREFIX_DELIMITER +
                partition +
                PARTITION_SUFIX_DELIMITER +
                extension;
    }

    private String createIndexRemovedPathName(long partition, String extension) {
        return FILE_STORAGE_DIRECTORY +
                INDEX_DIRECTORY +
                "/" +
                INDEX_REMOVED_FILE_NAME +
                PARTITION_PREFIX_DELIMITER +
                partition +
                PARTITION_SUFIX_DELIMITER +
                extension;
    }

    @Override
    public Long updateOffset(Long size) {
        return this.currentOffset += size;
    }

    @Override
    public Long getOffset(String filePath) {
        return nonNull(filePath) && !filePath.isBlank() && PathType.QUEUE_PATH.getPath().equals(filePath)
                ? this.currentOffset
                : fileOffset(filePath);
    }
}
