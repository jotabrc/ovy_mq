package io.github.jotabrc.ovy_mq_client.component.session.interfaces;

import io.github.jotabrc.ovy_mq_client.component.session.SessionType;
import io.github.jotabrc.ovy_mq_core.domain.Client;

import java.util.concurrent.CompletableFuture;

public interface SessionTimeoutManager {

    CompletableFuture<SessionManager> manage(SessionManager session, Client client, CompletableFuture<SessionManager> finalFuture);
    SessionType supports();
}
