package io.github.jotabrc.ovy_mq.queue.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jotabrc.ovy_mq.queue.repository.interfaces.FileRepository;
import io.github.jotabrc.ovy_mq.queue.util.FilePath;
import io.github.jotabrc.ovy_mq.queue.util.FilePathHelper;
import io.github.jotabrc.ovy_mq_core.domain.IndexData;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Repository
public class FileImpl implements FileRepository {

    private final ObjectMapper objectMapper;
    private final FilePathHelper filePathHelper;

    @Override
    public Path filePath(String filePath) {
        return Paths.get(new ClassPathResource(filePath).getPath());
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
                throw new OvyException.DirectoryCreation("Error while creating directory", e.getMessage(), directoryPath.toString());
            }
        }

        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                throw new OvyException.FileCreation("Error while creating file", e.getMessage(), filePath.toString());
            }
        }

        return filePath;
    }

    @Override
    public boolean delete(Path filePath) {
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new OvyException.FileDeletion("Error while deleting file", e.getMessage(), filePath.toString());
        }
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
            throw new OvyException.OffsetOperation("Error while reading file size", e.getMessage(), filePath.toString());
        }
    }

    @Override
    public <T> IndexData writeQueue(T data, String path) {
        try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(path, true))) {
            byte[] payload = objectMapper.writeValueAsBytes(data);
            IndexData indexData = new IndexData(null, payload.length, null, null, OffsetDateTime.now(), filePathHelper.extractPartition(path));
            writer.write(payload);
            return indexData;
        } catch (FileNotFoundException e) {
            throw new OvyException.WriteOperation("File not found", e.getMessage(), path);
        } catch (IOException e) {
            throw new OvyException.WriteOperation("Error while writing to file", e.getMessage(), path);
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
            throw new OvyException.ReadOperation("File not found", e.getMessage(), path);
        } catch (IOException e) {
            throw new OvyException.ReadOperation("Error while reading file", e.getMessage(), path);
        }
    }

    @Override
    public IndexData writeIndex(IndexData data, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            writer.write(data.id() + "," + data.size() + "," + data.offset() + "," + data.topic() + "," + data.storedAt());
            writer.newLine();
            return data;
        } catch (IOException e) {
            throw new OvyException.WriteOperation("Error while writing to file", e.getMessage(), path);
        }
    }

    @Override
    public IndexData readIndexByIdAndGetFirst(String id, Set<String> paths) {
        for (String path : paths) {
            try (BufferedReader reader = getIndexReader(path)) {
                IndexData data;
                while (nonNull(data = readIndexNextLine(reader, path))) {
                    if (Objects.equals(id, data.id())) {
                        return data;
                    }
                }
            } catch (FileNotFoundException e) {
                throw new OvyException.ReadOperation("File not found", e.getMessage(), path);
            } catch (IOException e) {
                throw new OvyException.ReadOperation("Error while reading file", e.getMessage(), path);
            }
        }
        return null;
    }

    @Override
    public IndexData readIndexByTopicAndGetFirst(String topic, Set<String> paths) {
        for (String path : paths) {
            try (BufferedReader reader = getIndexReader(path)) {
                IndexData data;
                while (nonNull(data = readIndexNextLine(reader, path))) {
                    if (Objects.equals(topic, data.topic())) {
                        return data;
                    }
                }
            } catch (FileNotFoundException e) {
                throw new OvyException.ReadOperation("File not found", e.getMessage(), path);
            } catch (IOException e) {
                throw new OvyException.ReadOperation("Error while reading file", e.getMessage(), path);
            }
        }
        return null;
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
    public IndexData readIndexNextLine(BufferedReader reader, String path) {
        String line;
        try {
            if (nonNull(line = reader.readLine())) {
                String[] values = line.split(",");
                if (Objects.equals(4, values.length)) {
                    String id = values[0];
                    if (isRemoved(id, path)) return null;
                    Integer size = Integer.parseInt(values[1]);
                    Long offset = Long.parseLong(values[2]);
                    String topic = values[3];
                    OffsetDateTime storedAt = OffsetDateTime.parse(values[4]);
                    return new IndexData(id, size, offset, topic, storedAt, filePathHelper.extractPartition(path));
                }
            }
        } catch (IOException e) {
            throw new OvyException.ReadOperation("Error while reading file", e.getMessage(), "");
        }
        return null;
    }

    @Override
    public Set<String> readIndexAndGetAllIds(BufferedReader reader, String path) {
        Set<String> ids = new HashSet<>();
        while (true) {
            IndexData data = readIndexNextLine(reader, path);
            if (isNull(data)) break;
            ids.add(data.id());
        }
        return ids;
    }

    @Override
    public List<String> readPaths(BufferedReader reader, String path) {
        List<String> paths = new ArrayList<>();
        String line;
        while(true) {
            try {
                if (!nonNull(line = reader.readLine())) break;
                paths.add(line);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return paths;
    }

    @Override
    public boolean exists(BufferedReader reader, String id, String path) {
        while (true) {
            IndexData data = readIndexNextLine(reader, path);
            if (isNull(data)) break;
            if (Objects.equals(id, data.id())) return true;
        }
        return false;
    }

    @Override
    public boolean isRemoved(String id, String indexPath) {
        long partition = filePathHelper.extractPartition(indexPath);
        String path = FilePath.INDEX_REMOVED_PATH.withPartition(partition);
        if (Objects.equals(indexPath, path)) return false;
        try (BufferedReader reader = getIndexReader(path)) {
            IndexData data;
            while (nonNull(data = readIndexNextLine(reader, path))) {
                if (Objects.equals(id, data.id())) {
                    return true;
                }
            }
            return false;
        } catch (FileNotFoundException e) {
            throw new OvyException.ReadOperation("File not found", e.getMessage(), path);
        } catch (IOException e) {
            throw new OvyException.ReadOperation("Error while reading file", e.getMessage(), path);
        }
    }
}
