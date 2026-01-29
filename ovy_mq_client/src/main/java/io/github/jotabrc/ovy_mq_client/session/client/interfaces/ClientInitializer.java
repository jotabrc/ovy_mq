package io.github.jotabrc.ovy_mq_client.session.client.interfaces;

import io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces.ClientHelperAware;
import io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces.ClientSessionAware;
import io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces.ClientStateAware;
import io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.ManagerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

public interface ClientInitializer<T, U, V> extends ClientHelperAware<T>, ClientStateAware<T, U, V>, ClientSessionAware<T, U, V> {

    List<ScheduledFuture<?>> initializeManagers(List<ManagerFactory> factories);
    CompletableFuture<ClientHelper<T>> initializeSession();
}
