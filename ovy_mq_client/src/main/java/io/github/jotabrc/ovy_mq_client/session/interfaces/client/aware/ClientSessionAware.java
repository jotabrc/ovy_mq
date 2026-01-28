package io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientSession;

public interface ClientSessionAware<T, U, V> {

    void setClientSession(ClientSession<T, U, V> clientSession);
}
