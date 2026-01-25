package io.github.jotabrc.ovy_mq_client.session.interfaces.client;

public interface ClientMessageSender<T, U, V> {

    void send(String destination, Object payload);
    void setClientAdapter(ClientAdapter<T, U, V> clientAdapter);
}
