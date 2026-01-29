package io.github.jotabrc.ovy_mq_client.session.client.interfaces;

public interface ClientAdapter<T, U, V> {

    ClientSession<T, U, V> getClientSession();
    ClientState<T, U, V> getClientState();
    ClientHelper<T> getClientHelper();
    ClientInitializer<T, U, V> getClientInitializer();
    ClientMessageSender<T, U, V> getClientMessageSender();
}
