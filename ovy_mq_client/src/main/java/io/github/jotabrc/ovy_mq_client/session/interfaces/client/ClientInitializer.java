package io.github.jotabrc.ovy_mq_client.session.interfaces.client;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware.ClientHelperAware;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware.ClientSessionAware;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware.ClientStateAware;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.ManagerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

public interface ClientInitializer<T, U, V> extends ClientHelperAware<T>, ClientStateAware<T, U, V>, ClientSessionAware<T, U, V> {

    List<ScheduledFuture<?>> initializeManagers(List<ManagerFactory> factories);
    CompletableFuture<ClientHelper<T>> initializeSession();
}
