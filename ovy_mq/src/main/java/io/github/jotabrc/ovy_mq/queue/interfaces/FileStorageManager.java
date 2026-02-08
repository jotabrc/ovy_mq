package io.github.jotabrc.ovy_mq.queue.interfaces;

import java.nio.file.Path;

public interface FileStorageManager {

    void initialize();
    void rebuild(Path filePath);
    Long updateOffset(Long size, Long partition);
    Long getOffset(String filePath);
    Long getOffset(Long partition);
}
