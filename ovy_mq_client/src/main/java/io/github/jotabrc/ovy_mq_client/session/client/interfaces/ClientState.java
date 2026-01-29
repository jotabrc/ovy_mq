package io.github.jotabrc.ovy_mq_client.session.client.interfaces;

import io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces.ClientHelperAware;

import java.util.concurrent.CompletableFuture;

public interface ClientState<T, U, V> extends ClientHelperAware<T> {

    CompletableFuture<T> connect(String url, U headers, V sessionHandler);
    boolean isConnected();
    boolean disconnect(boolean force);
    void stop();
    boolean destroy(boolean force);
}
