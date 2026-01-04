package io.github.jotabrc.ovy_mq_client.session.interfaces;

public interface SessionConnection {

    boolean isConnected();
    boolean disconnect(boolean force);
    boolean destroy(boolean force);
}