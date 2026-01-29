package io.github.jotabrc.ovy_mq_client.session.interfaces;

import io.github.jotabrc.ovy_mq_client.session.client.impl.SessionType;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientHelper;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientSession;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientState;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;

import java.util.concurrent.CompletableFuture;

public interface SessionTimeoutManager<T, U, V> {

    CompletableFuture<ClientHelper<T>> execute(Client client,
                                               ClientState<T, U, V> clientState,
                                               ClientHelper<T> clientHelper,
                                               ClientSession<T, U, V> clientSession);
    SessionType supports();
}
