package io.github.jotabrc.ovy_mq_client.session.interfaces.client;

public interface ClientSession<T, U, V> {
    void setClientAdapter(ClientAdapter<T, U, V> clientAdapter);
}
