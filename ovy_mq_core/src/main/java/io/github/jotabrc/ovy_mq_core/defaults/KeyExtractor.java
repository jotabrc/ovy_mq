package io.github.jotabrc.ovy_mq_core.defaults;

@FunctionalInterface
public interface KeyExtractor<T, U, R> {

    R apply(T t, U u, Class<R> r);
}
