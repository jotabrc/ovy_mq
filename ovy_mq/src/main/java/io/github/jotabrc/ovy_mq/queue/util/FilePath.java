package io.github.jotabrc.ovy_mq.queue.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum FilePath {

    QUEUE_PATH(FilePathHelper.FILE_STORAGE_DIRECTORY +
            FilePathHelper.QUEUE_DIRECTORY +
            "/" +
            FilePathHelper.QUEUE_FILE_NAME +
            FilePathHelper.PARTITION_PREFIX_DELIMITER +
            FilePathHelper.PARTITION_PLACEHOLDER +
            FilePathHelper.PARTITION_SUFIX_DELIMITER +
            FilePathHelper.QUEUE_FILE_EXTENSION),

    INDEX_PATH(FilePathHelper.FILE_STORAGE_DIRECTORY +
            FilePathHelper.INDEX_DIRECTORY +
            "/" +
            FilePathHelper.INDEX_FILE_NAME +
            FilePathHelper.PARTITION_PREFIX_DELIMITER +
            FilePathHelper.PARTITION_PLACEHOLDER +
            FilePathHelper.PARTITION_SUFIX_DELIMITER +
            FilePathHelper.INDEX_FILE_EXTENSION),

    INDEX_REMOVED_PATH(FilePathHelper.FILE_STORAGE_DIRECTORY +
            FilePathHelper.INDEX_DIRECTORY +
            "/" +
            FilePathHelper.INDEX_REMOVED_FILE_NAME +
            FilePathHelper.PARTITION_PREFIX_DELIMITER +
            FilePathHelper.PARTITION_PLACEHOLDER +
            FilePathHelper.PARTITION_SUFIX_DELIMITER +
            FilePathHelper.INDEX_FILE_EXTENSION),

    QUEUE_PATH_TMP(FilePathHelper.FILE_STORAGE_DIRECTORY +
            FilePathHelper.QUEUE_DIRECTORY +
            "/" +
            FilePathHelper.QUEUE_FILE_NAME +
            FilePathHelper.PARTITION_PREFIX_DELIMITER +
            FilePathHelper.PARTITION_PLACEHOLDER +
            FilePathHelper.PARTITION_SUFIX_DELIMITER +
            FilePathHelper.TEMP_FILE_EXTENSION),

    INDEX_PATH_TMP(FilePathHelper.FILE_STORAGE_DIRECTORY +
            FilePathHelper.INDEX_DIRECTORY +
            "/" +
            FilePathHelper.INDEX_FILE_NAME +
            FilePathHelper.PARTITION_PREFIX_DELIMITER +
            FilePathHelper.PARTITION_PLACEHOLDER +
            FilePathHelper.PARTITION_SUFIX_DELIMITER +
            FilePathHelper.INDEX_FILE_EXTENSION),

    INDEX_REMOVED_PATH_TMP(FilePathHelper.FILE_STORAGE_DIRECTORY +
            FilePathHelper.INDEX_DIRECTORY +
            "/" +
            FilePathHelper.INDEX_REMOVED_FILE_NAME +
            FilePathHelper.PARTITION_PREFIX_DELIMITER +
            FilePathHelper.PARTITION_PLACEHOLDER +
            FilePathHelper.PARTITION_SUFIX_DELIMITER +
            FilePathHelper.TEMP_FILE_EXTENSION);

    private final String path;

    public String withPartition(final long partition) {
        return new ClassPathResource(this.path.replace(FilePathHelper.PARTITION_PLACEHOLDER, String.valueOf(partition))).getPath();
    }

    public static Set<FilePath> getNonTempFilePath() {
        return Arrays.stream(FilePath.values())
                .filter(filePath -> !filePath.name().endsWith("TMP"))
                .collect(Collectors.toSet());
    }
}