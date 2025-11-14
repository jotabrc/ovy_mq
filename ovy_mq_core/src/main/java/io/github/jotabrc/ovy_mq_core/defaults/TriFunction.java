package io.github.jotabrc.ovy_mq_core.defaults;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {

    R apply(T t, U u, V v);
}
