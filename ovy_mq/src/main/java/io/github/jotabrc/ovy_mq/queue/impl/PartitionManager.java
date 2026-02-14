package io.github.jotabrc.ovy_mq.queue.impl;

import io.github.jotabrc.ovy_mq.queue.util.FilePath;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    private void init() {
        initialize();
    }

    public void initialize() {
        for (int i = 0; i < partitionsQuantity; i++) {
            final int partitionNumber = i;
            for (FilePath path : FilePath.getNonTempFilePath())
                this.partitions.compute(path, (key, set) -> {
                    if (isNull(set)) set = new HashSet<>();
                    set.add(path.withPartition(partitionNumber));
                    return set;
                });
        }
    }

    public List<String> getPartitionsFor(FilePath filePath) {
        List<String> partitionsToShuffle = new ArrayList<>(partitions.get(filePath));
        Collections.shuffle(partitionsToShuffle);
        return partitionsToShuffle;
    }

    public long getPartitionToUse() {
        return partitionToUse.getAndUpdate(value -> {
            long increment = value + 1;
            return (increment >= partitionsQuantity)
                    ? 0L
                    : increment;
        });
    }
}