package io.github.jotabrc.ovy_mq_client.session.interfaces.client;

import io.github.jotabrc.ovy_mq_client.session.manager_handler.ManagerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

public interface ClientInitializer<T, U, V> {

    List<ScheduledFuture<?>> initializeManagers(List<ManagerFactory> factories);
    CompletableFuture<ClientHelper<?>> initializeSession();
    void setClientAdapter(ClientAdapter<T, U, V> clientAdapter);
}
