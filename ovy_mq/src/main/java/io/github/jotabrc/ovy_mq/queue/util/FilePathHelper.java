package io.github.jotabrc.ovy_mq.queue.util;

import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class FilePathHelper {

    public static final Pattern PATTERN_PARTITION = Pattern.compile("___\\{\\{\\{([0-9]+)}}}___");

    public static final String PARTITION_PREFIX_DELIMITER = "___{{{";
    public static final String PARTITION_SUFIX_DELIMITER = "}}}___";
    public static final String PARTITION_PLACEHOLDER = "{{PARTITION_NUMBER}}";

    public static final String FILE_STORAGE_DIRECTORY = "/filestorage";

    public static final String QUEUE_DIRECTORY = "/queue";
    public static final String QUEUE_FILE_NAME = "queue";
    public static final String QUEUE_FILE_EXTENSION = ".bin";

    public static final String INDEX_DIRECTORY = "/index";
    public static final String INDEX_REMOVED_DIRECTORY = "/indexremoved";
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
            log.info("Extracting partition number from path={}", filePath);
            Matcher matcher = PATTERN_PARTITION.matcher(filePath);
            String partition = null;
            if (matcher.find()) partition = matcher.group(1);
            Objects.requireNonNull(partition);
            log.info("Partition number={} extracted from path={}", partition, filePath);
            return Long.parseLong(partition);
        } catch (NumberFormatException e) {
            throw new OvyException.PartitionExtraction("Error extracting partition from %s: %s".formatted(filePath, e.getMessage()));
        }
    }
}
