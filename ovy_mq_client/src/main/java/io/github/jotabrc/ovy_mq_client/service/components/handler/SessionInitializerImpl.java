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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class SessionInitializerImpl implements SessionInitializer {

    private final ObjectProvider<SessionManager> sessionManagerProvider;
    private final HeadersFactoryResolver headersFactoryResolver;

    @Value("${ovymq.session.connection.timeout}")
    private Long connectionTimeout;

    @Value("${ovymq.session.connection.backoff}")
    private Integer connectionBackoff;

    @Override
    public SessionManager initialize(Client client) {
        log.info("Initializing-session client={}", client.getId());
        AtomicInteger counter = new AtomicInteger(0);
        SessionManager sessionManager = sessionManagerProvider.getObject();

        while (counter.getAndIncrement() <= connectionBackoff) {
            try {
                sessionManager.setClient(client);
                client.setLastHealthCheck(OffsetDateTime.now());
                sessionManager.initialize()
                        .whenComplete((manager, exception) -> {
                            if (isNull(manager) || !manager.isConnected()) {
                                throw new ServerSubscribeException("Server not ready");
                            }
                        })
                        .orTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                        .exceptionally(e -> {
                            log.error("Server unavailable");
                            return null;
                        });
                log.info("Session initialized: client={} topic={}", client.getId(), client.getTopic());
                return sessionManager;
            } catch (Exception e) {
                log.info("Server is unavailable, retrying connection. Retry-number={} client={} topic={}", counter.get(), client.getId(), client.getTopic());
            }
        }
        return null;
    }
}
