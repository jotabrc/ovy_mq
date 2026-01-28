package io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientInitializer;

public interface ClientInitializerAware<T, U, V> {

    void setClientInitializer(ClientInitializer<T, U, V> clientInitializer);
}
