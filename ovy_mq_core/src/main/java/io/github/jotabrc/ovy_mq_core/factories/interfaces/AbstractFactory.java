package io.github.jotabrc.ovy_mq_core.factories.interfaces;

public interface AbstractFactory<T, R> {

    R create(T dto);
    Class<T> supports();
}