package io.github.jotabrc.ovy_mq.queue.interfaces;

public interface FileStorageManager {

    void initialize();
    void rebuild(String filePath);
    Long updateOffset(Long size, Long partition);
    Long getOffset(String filePath);
    Long getOffset(Long partition);
}
