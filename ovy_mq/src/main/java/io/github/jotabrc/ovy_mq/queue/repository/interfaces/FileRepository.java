package io.github.jotabrc.ovy_mq.queue.repository.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.IndexData;

import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface FileRepository {

    Path filePath(String filePath);
    Path directoryPath(Path filePath);
    Path createFile(String filePath);
    Path createFile(Path filePath);
    boolean delete(Path filePath);
    Long fileOffset(String filePath);
    Long fileOffset(Path filePath);
    <T> IndexData writeQueue(T data, String path);
    <T> T readQueueAndGet(IndexData data, Class<T> target, String path);
    IndexData writeIndex(IndexData data, String path);
    IndexData readIndexByIdAndGetFirst(String id, List<String> paths);
    IndexData readIndexByTopicAndGetFirst(String topic, List<String> paths);
    BufferedReader getIndexReader(String path);
    IndexData readIndexNextLine(BufferedReader reader, String path);
    IndexData readIndexNextLine(BufferedReader reader, String path, boolean checkRemoved);
    Set<String> readIndexAndGetAllIds(BufferedReader reader, String path);
    List<String> readPaths(BufferedReader reader, String path);
    boolean exists(BufferedReader reader, String id, String path);
    boolean isRemoved(String id, String indexPath, boolean checkRemoved);
}
