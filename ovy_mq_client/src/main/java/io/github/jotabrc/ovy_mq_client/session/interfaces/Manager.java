package io.github.jotabrc.ovy_mq_client.session.interfaces;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientInitializer;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientState;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.ManagerFactory;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;

import java.util.concurrent.ScheduledFuture;

public interface Manager<T, U, V> {

    ScheduledFuture<?> execute(Client client,
                               ClientState<T, U, V> clientState,
                               ClientInitializer<T, U, V> clientInitializer);
    ManagerFactory factory();
}
