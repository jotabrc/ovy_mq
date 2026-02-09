package io.github.jotabrc.ovy_mq.queue.impl;

import io.github.jotabrc.ovy_mq.queue.util.FilePath;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
public class PartitionManager {

    @Value("${ovymq.partition.quantity:4}")
    private Long partitionsQuantity;
    private final AtomicLong partitionToUse = new AtomicLong(0);
    private final Map<FilePath, Set<String>> partitions = new HashMap<>();

    public Set<String> initialize() {
        for (int i = 0; i < partitionsQuantity; i++) {
            final int partitionNumber = i;
            for (FilePath path : FilePath.getNonTempFilePath())
                this.partitions.compute(path, (key, set) -> {
                    if (isNull(set)) set = new HashSet<>();
                    set.add(path.withPartition(partitionNumber));
                    return set;
                });
        }
        return this.getPartitionsFor(FilePath.INDEX_PATH);
    }

    public Set<String> getPartitionsFor(FilePath filePath) {
        return new HashSet<>(partitions.get(filePath));
    }

    public long getPartitionToUse() {
        long position = partitionToUse.getAndIncrement();
        if (Objects.equals(partitionsQuantity, partitionToUse.get())) partitionToUse.set(0);
        else if (partitionToUse.get() > partitionsQuantity) partitionToUse.set(0);
        if (position > partitionsQuantity) position = 0L;
        return position;
    }
}
