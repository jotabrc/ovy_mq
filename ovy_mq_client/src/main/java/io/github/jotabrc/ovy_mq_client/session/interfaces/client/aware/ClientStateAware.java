package io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientState;

public interface ClientStateAware<T, U, V> {

    void setClientState(ClientState<T, U, V> clientState);
}
