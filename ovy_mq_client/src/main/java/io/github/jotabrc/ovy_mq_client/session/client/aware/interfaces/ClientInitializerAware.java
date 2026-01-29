package io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces;

import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientInitializer;

public interface ClientInitializerAware<T, U, V> {

    void setClientInitializer(ClientInitializer<T, U, V> clientInitializer);
}
