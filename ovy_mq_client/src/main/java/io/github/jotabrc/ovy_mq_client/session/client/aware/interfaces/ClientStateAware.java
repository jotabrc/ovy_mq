package io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces;

import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientState;

public interface ClientStateAware<T, U, V> {

    void setClientState(ClientState<T, U, V> clientState);
}
