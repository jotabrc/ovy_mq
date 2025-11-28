package io.github.jotabrc.ovy_mq_core.util;

@FunctionalInterface
public interface AbstractTriFunctionFactory<T, U, V, R> {

    public R create(T t, U u, V v);
}
