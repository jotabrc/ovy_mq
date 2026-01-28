package io.github.jotabrc.ovy_mq_client.session.interfaces.client;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware.ClientHelperAware;

import java.util.concurrent.CompletableFuture;

public interface ClientState<T, U, V> extends ClientHelperAware<T> {

    CompletableFuture<T> connect(String url, U headers, V sessionHandler);
    boolean isConnected();
    boolean disconnect(boolean force);
    void stop();
    boolean destroy(boolean force);
}
