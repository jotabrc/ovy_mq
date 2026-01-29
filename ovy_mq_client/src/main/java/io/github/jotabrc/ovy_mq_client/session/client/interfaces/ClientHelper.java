package io.github.jotabrc.ovy_mq_client.session.client.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.client.Client;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

public interface ClientHelper<T> {

    Client getClient();
    List<String> getSubscriptions();
    T getSession();
    CompletableFuture<ClientHelper<T>> getConnectionFuture();
    List<ScheduledFuture<?>> getScheduledFutures();
    String getClientId();
    void setClient(Client client);
    void setSubscriptions(List<String> subscriptions);
    void setSession(T t);
    void setConnectionFuture(CompletableFuture<ClientHelper<T>> connectionFuture);
    void setScheduledFutures(List<ScheduledFuture<?>> scheduledFutures);
}
