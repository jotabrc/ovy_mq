package io.github.jotabrc.ovy_mq.repository.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.IndexData;

import java.io.BufferedReader;
import java.util.Set;

public interface FileRepository {

    <T> IndexData writeQueue(T data, String path);
    <T> T readQueueAndGet(IndexData data, Class<T> target, String path);
    IndexData writeIndex(IndexData data, String path);
    IndexData readIndexByIdAndGetFirst(String id, String path);
    IndexData readIndexByTopicAndGetFirst(String topic, String path);
    BufferedReader getIndexReader(String path);
    IndexData readIndexNextLine(BufferedReader reader);
    Set<String> readIndexAndGetAllIds(BufferedReader reader);
    boolean exists(BufferedReader reader, String id);
}
