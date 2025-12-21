package io.github.jotabrc.ovy_mq_client.session.interfaces;

import io.github.jotabrc.ovy_mq_client.session.SessionType;

import java.util.concurrent.CompletableFuture;

public interface ConnectionManager<T, U, V> {

    CompletableFuture<T> connect(String url, U headers, V sessionHandler);
    void stop();
    SessionType supports();
}
