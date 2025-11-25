package io.github.jotabrc.ovy_mq_client.component.session;

import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class SessionTimeoutManager {

    @Value("${ovymq.session.connection.backoff}")
    protected Integer maxRetries;

    private final ScheduledExecutorService scheduledExecutor;

    public void manage(Supplier<CompletableFuture<SessionManager>> connect, Client client) {
        final CompletableFuture<SessionManager> finalFuture = new CompletableFuture<>();
        connect(finalFuture, connect, client, 1);
    }

    private void connect(CompletableFuture<SessionManager> finalFuture,
                         Supplier<CompletableFuture<SessionManager>> connect,
                         Client client,
                         int attempt) {
        if (attempt > maxRetries) {
            finalFuture.completeExceptionally(new TimeoutException("Connection failed attempt=%d".formatted(attempt)));
            return;
        }
        connect.get().whenComplete(((sessionManager, throwable) -> {
            if (isNull(throwable)) {
                finalFuture.complete(sessionManager);
                log.info("Connected to server: client={} topic={}", client.getId(), client.getTopic());
            } else {
                log.info("Connection attempt failed: client={} topic={}", client.getId(), client.getTopic(), throwable);
            }
        }));
        scheduledExecutor.schedule(() -> connect(finalFuture, connect, client, attempt + 1),
                client.getTimeout(),
                TimeUnit.MILLISECONDS);
    }
}
