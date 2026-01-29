package io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces;

import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientSession;

public interface ClientSessionAware<T, U, V> {

    void setClientSession(ClientSession<T, U, V> clientSession);
}
