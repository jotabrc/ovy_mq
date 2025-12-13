package io.github.jotabrc.ovy_mq_core.domain.concurrency;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class ThreadLock {

    private final String key;
}