package io.github.jotabrc.ovy_mq.repository.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;

@RequiredArgsConstructor
@Getter
public enum PathType {

    QUEUE_PATH(new ClassPathResource("/queue/ovy_mq_queue.bin").getPath()),
    INDEX_PATH(new ClassPathResource("/queue/ovy_mq_index.txt").getPath()),
    INDEX_REMOVED_PATH(new ClassPathResource("/queue/ovy_mq_index_removed.txt").getPath()),
    QUEUE_PATH_TMP(new ClassPathResource("/queue/ovy_mq_queue.tmp").getPath()),
    INDEX_PATH_TMP(new ClassPathResource("/queue/ovy_mq_index.tmp").getPath()),
    INDEX_REMOVED_PATH_TMP(new ClassPathResource("/queue/ovy_mq_index_removed.tmp").getPath());

    private final String path;

    public String getOpposite() {
        return switch (this) {
            case INDEX_PATH_TMP -> INDEX_PATH.getPath();
            case QUEUE_PATH_TMP -> QUEUE_PATH.getPath();
            case INDEX_REMOVED_PATH_TMP -> INDEX_REMOVED_PATH.getPath();
            default -> null;
        };
    }
}
