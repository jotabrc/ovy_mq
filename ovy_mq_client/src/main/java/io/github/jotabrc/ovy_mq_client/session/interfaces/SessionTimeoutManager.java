package io.github.jotabrc.ovy_mq_client.session.interfaces;

import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;

import java.util.concurrent.CompletableFuture;

public interface SessionTimeoutManager {

    CompletableFuture<SessionManager> execute(SessionManager session, Client client, CompletableFuture<SessionManager> finalFuture);
    SessionType supports();
}
