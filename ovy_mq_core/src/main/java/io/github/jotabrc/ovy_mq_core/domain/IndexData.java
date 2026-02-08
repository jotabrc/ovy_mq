package io.github.jotabrc.ovy_mq_core.domain;

import java.time.OffsetDateTime;

public record IndexData(String id, Integer size, Long offset, String topic, OffsetDateTime storedAt, Long partitionNumber) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        IndexData indexData = (IndexData) o;
        return id.equals(indexData.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode() * 31;
    }
}
