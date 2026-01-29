package io.github.jotabrc.ovy_mq_client.session.client.interfaces;

import io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.ManagerFactory;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;

import java.util.concurrent.ScheduledFuture;

public interface Manager<T, U, V> {

    ScheduledFuture<?> execute(Client client,
                               ClientState<T, U, V> clientState,
                               ClientInitializer<T, U, V> clientInitializer);
    ManagerFactory factory();
}
