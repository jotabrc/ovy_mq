package io.github.jotabrc.ovy_mq_client.session.interfaces;

import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientHelper;

import java.util.concurrent.CompletableFuture;

public interface SessionTimeoutManager<T, U, V> {

    CompletableFuture<ClientHelper<?>> execute(ClientAdapter<T, U, V> clientAdapter);
    SessionType supports();
}
