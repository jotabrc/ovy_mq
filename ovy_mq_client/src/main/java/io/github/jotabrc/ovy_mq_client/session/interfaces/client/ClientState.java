package io.github.jotabrc.ovy_mq_client.session.interfaces.client;

import java.util.concurrent.CompletableFuture;

public interface ClientState<T, U, V> {

    CompletableFuture<T> connect(String url, U headers, V sessionHandler);
    boolean isConnected();
    boolean disconnect(boolean force);
    void stop();
    boolean destroy(boolean force);
    void setClientAdapter(ClientAdapter<T, U, V> clientAdapter);
}
