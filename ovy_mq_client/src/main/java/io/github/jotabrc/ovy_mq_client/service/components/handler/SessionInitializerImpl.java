package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_client.handler.ServerSubscribeException;
import io.github.jotabrc.ovy_mq_client.service.components.HeadersFactoryResolver;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionInitializer;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class SessionInitializerImpl implements SessionInitializer {

    private final ObjectProvider<SessionManager> sessionManagerProvider;
    private final HeadersFactoryResolver headersFactoryResolver;

    @Override
    public SessionManager initialize(Client client) {
        log.info("Initializing-session client={}", client.getId());
        AtomicLong counter = new AtomicLong(0L);
        while (true) {
            SessionManager sessionManager = connect(client, counter);
            if (nonNull(sessionManager)) return sessionManager;
        }
    }

    private SessionManager connect(Client client, AtomicLong counter) {
        SessionManager sessionManager = sessionManagerProvider.getObject();
        try {
            sessionManager.setClient(client);
            client.setLastHealthCheck(OffsetDateTime.now());
            sessionManager.initialize()
                    .whenComplete((manager, exception) -> {
                                if (isNull(manager) || !manager.isConnected()) {
                                    throw new ServerSubscribeException("Server not ready");
                                }
                            }
                    ).get();
            log.info("Session initialized: client={} topic={}", client.getId(), client.getTopic());
            return sessionManager;
        } catch (Exception e) {
            log.info("Server is unavailable, retrying connection. Retry-number={} client={} topic={}", counter.getAndIncrement(), client.getId(), client.getTopic());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                log.error("Thread-interrupted={}: {}", Thread.interrupted(), ex.getMessage());
            }
        }
        return null;
    }
}
