package io.github.jotabrc.ovy_mq_client.service.handler;

import io.github.jotabrc.ovy_mq_client.domain.factory.WebSocketHttpHeaderFactory;
import io.github.jotabrc.ovy_mq_client.handler.ServerSubscribeException;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.SessionInitializer;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.service.registry.provider.ClientSessionRegistryProvider;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.*;
import static java.util.Objects.nonNull;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class SessionInitializerImpl implements SessionInitializer {

    private final ObjectProvider<SessionManager> sessionManagerProvider;
    private final WebSocketHttpHeaderFactory webSocketHttpHeaderFactory;
    private final ClientSessionRegistryProvider clientSessionRegistryProvider;

    @Override
    public void initialize(Client client) {
        log.info("Initializing-session client={}", client.getId());
        AtomicLong counter = new AtomicLong(0L);
        while (true) {
            if (connect(client, counter)) return;
        }
    }

    private boolean connect(Client client, AtomicLong counter) {
        WebSocketHttpHeaders headers = webSocketHttpHeaderFactory.get(client.getTopic());
        SessionManager sessionManager = sessionManagerProvider.getObject();

        try {
            sessionManager.setClient(client);
            client.setLastHealthCheck(OffsetDateTime.now());
            SessionManager session = connectToServerAndInitializeSubscription(sessionManager, headers);
            log.info("Session initialized: client={} topic={}", session.getClient().getId(), client.getTopic());
            return true;
        } catch (Exception e) {
            log.info("Server is unavailable, retrying connection. Retry-number={} client={} topic={}", counter.getAndIncrement(), client.getId(), client.getTopic());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                log.error("Thread-interrupted={}: {}", Thread.interrupted(), ex.getMessage());
            }
        }
        return false;
    }

    private SessionManager connectToServerAndInitializeSubscription(SessionManager sessionManager, WebSocketHttpHeaders headers) throws ExecutionException, InterruptedException {
        return sessionManager.connect("ws://localhost:9090/" + WS_REGISTRY, headers)
                .whenComplete((manager, exception) -> {
                    if (nonNull(manager) && manager.isConnected()) {
                        clientSessionRegistryProvider.addOrReplace(manager.getClient().getId(), sessionManager);
                    } else {
                        throw new ServerSubscribeException("Server not ready");
                    }
                }).get();
    }
}
