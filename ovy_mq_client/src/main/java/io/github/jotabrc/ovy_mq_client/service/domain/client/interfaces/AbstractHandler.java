package io.github.jotabrc.ovy_mq_client.service.domain.client.interfaces;

public interface AbstractHandler {

    default void execute() {
        throw new UnsupportedOperationException();
    }

    default <T> void execute(T t) {
        throw new UnsupportedOperationException();
    }

    default <T, R> void execute(T t, R r) {
        throw new UnsupportedOperationException();
    }

    default <T, R, K> void execute(T t, R r, K k) {
        throw new UnsupportedOperationException();
    }
}
