package io.github.jotabrc.ovy_mq.queue.util;

import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import org.springframework.stereotype.Component;

@Component
public class FilePathHelper {

    public static final String PARTITION_PREFIX_DELIMITER = "-";
    public static final String PARTITION_SUFIX_DELIMITER = "_";
    public static final String PARTITION_PLACEHOLDER = "{{PARTITION_NUMBER}}";

    public static final String FILE_STORAGE_DIRECTORY = "/filestorage";

    public static final String QUEUE_DIRECTORY = "/queue";
    public static final String QUEUE_FILE_NAME = "queue";
    public static final String QUEUE_FILE_EXTENSION = ".bin";

    public static final String INDEX_DIRECTORY = "/index";
    public static final String INDEX_FILE_NAME = "index";
    public static final String INDEX_REMOVED_FILE_NAME = "indexremoved";
    public static final String INDEX_FILE_EXTENSION = ".txt";

    public static final String PARTITION_DIRECTORY = "/partition";
    public static final String PARTITION_FILE_NAME = "partitions";
    public static final String PARTITION_FILE_EXTENSION = ".txt";

    public static final String TEMP_FILE_EXTENSION = ".tmp";

    public String createPath(FilePath type) {
        return type.getPath();
    }

    public String createPath(FilePath type, long partition) {
        return type.withPartition(partition);
    }

    public long extractPartition(String filePath) {
        try {
            String partition = filePath.substring(filePath.lastIndexOf(PARTITION_PREFIX_DELIMITER) + PARTITION_PREFIX_DELIMITER.length(),
                    filePath.lastIndexOf(PARTITION_SUFIX_DELIMITER));
            return Long.parseLong(partition);
        } catch (NumberFormatException e) {
            throw new OvyException.PartitionExtraction("Error extracting partition from %s: %s".formatted(filePath, e.getMessage()));
        }
    }
}
