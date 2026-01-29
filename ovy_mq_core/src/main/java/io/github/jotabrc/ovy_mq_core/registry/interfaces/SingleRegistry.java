package io.github.jotabrc.ovy_mq_core.registry.interfaces;

import java.util.Optional;

@Deprecated
public interface SingleRegistry<T> {

    void add(T t);
    Optional<T> get();
    String getId();
}
