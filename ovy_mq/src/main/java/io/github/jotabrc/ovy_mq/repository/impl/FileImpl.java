package io.github.jotabrc.ovy_mq.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jotabrc.ovy_mq.repository.interfaces.FileRepository;
import io.github.jotabrc.ovy_mq_core.domain.IndexData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.OffsetDateTime;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Service
public class FileImpl implements FileRepository {

    private final ObjectMapper objectMapper;

    @Override
    public <T> IndexData writeQueue(T data, String path) {
        try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(path, true))) {
            byte[] payload = objectMapper.writeValueAsBytes(data);
            IndexData indexData = new IndexData(null, payload.length, null, null, OffsetDateTime.now());
            writer.write(payload);
            return indexData;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T readQueueAndGet(IndexData data, Class<T> target, String path) {
        try (RandomAccessFile reader = new RandomAccessFile(path, "r")) {
            reader.seek(data.offset());
            byte[] payload = new byte[data.size()];
            reader.readFully(payload);
            return objectMapper.convertValue(payload, target);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IndexData writeIndex(IndexData data, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            writer.write(data.id() + "," + data.size() + "," + data.offset() + "," + data.topic() + "," + data.storedAt());
            writer.newLine();
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IndexData readIndexByIdAndGetFirst(String id, String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while (nonNull(line = reader.readLine())) {
                String[] values = line.split(",");
                String storedId = values[0];

                if (Objects.equals(id, storedId)) {
                    Integer size = Integer.parseInt(values[1]);
                    Long offset = Long.parseLong(values[2]);
                    String storedTopic = values[3];
                    OffsetDateTime storedAt = OffsetDateTime.parse(values[4]);
                    return new IndexData(storedId, size, offset, storedTopic, storedAt);
                }
            }
            return null;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IndexData readIndexByTopicAndGetFirst(String topic, String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while (nonNull(line = reader.readLine())) {
                String[] values = line.split(",");

                String storedTopic = values[3];
                if (Objects.equals(topic, storedTopic)) {
                    String storedId = values[0];
                    Integer size = Integer.parseInt(values[1]);
                    Long offset = Long.parseLong(values[2]);
                    OffsetDateTime storedAt = OffsetDateTime.parse(values[4]);
                    return new IndexData(storedId, size, offset, storedTopic, storedAt);
                }
            }
            return null;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BufferedReader getIndexReader(String path) {
        try {
            return new BufferedReader(new FileReader(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IndexData readIndexNextLine(BufferedReader reader) {
        String line;
        try {
            if (nonNull(line = reader.readLine())) {
                String[] values = line.split(",");
                if (Objects.equals(4, values.length)) {
                    String storedId = values[0];
                    Integer size = Integer.parseInt(values[1]);
                    Long offset = Long.parseLong(values[2]);
                    String storedTopic = values[3];
                    OffsetDateTime storedAt = OffsetDateTime.parse(values[4]);
                    return new IndexData(storedId, size, offset, storedTopic, storedAt);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Set<String> readIndexAndGetAllIds(BufferedReader reader) {
        Set<String> ids = new HashSet<>();
        while (true) {
            IndexData data = readIndexNextLine(reader);
            if (isNull(data)) break;
            ids.add(data.id());
        }
        return ids;
    }

    @Override
    public boolean exists(BufferedReader reader, String id) {
        while (true) {
            IndexData data = readIndexNextLine(reader);
            if (isNull(data)) break;
            if (Objects.equals(id, data.id())) return true;
        }
        return false;
    }
}
