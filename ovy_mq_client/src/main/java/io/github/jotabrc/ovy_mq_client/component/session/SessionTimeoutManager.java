package io.github.jotabrc.ovy_mq_client.component.session;

import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class SessionTimeoutManager {

    @Value("${ovymq.session.connection.timeout}")
    protected Long connectionTimeout;

    @Value("${ovymq.session.connection.backoff}")
    protected Integer connectionBackoff;

    public CompletableFuture<SessionManager> manage(CompletableFuture<SessionManager> future, Runnable connect, Callable<Boolean> isConnected, Client client) {
        int counter = 1;
        while (counter++ < connectionBackoff) {
            try {
                if (isConnected.call()) break;
                future = new CompletableFuture<>();
                connect.run();
                future.orTimeout(client.getTimeout(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.info("Server is unavailable, retrying connection: client={} topic={}", client.getId(), client.getTopic(), e);
            }
        }
        return future;
    }
}
