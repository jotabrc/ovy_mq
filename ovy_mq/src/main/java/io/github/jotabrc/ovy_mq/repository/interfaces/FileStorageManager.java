package io.github.jotabrc.ovy_mq.repository.interfaces;

import java.nio.file.Path;

public interface FileStorageManager {

    Path filePath(String filePath);
    Path directoryPath(Path filePath);
    Path createFile(String filePath);
    Path createFile(Path filePath);
    Long fileOffset(String filePath);
    Long fileOffset(Path filePath);
    Long initialize(String filePath, boolean getOffset);
    void rebuild(String filePath);
    Long updateOffset(Long size);
    Long getOffset(String filePath);
}
