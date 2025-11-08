package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionInitializer;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.service.registry.ClientSessionRegistry;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageDispatcher {

    private final SessionInitializer sessionInitializer;
    private final ClientSessionRegistry clientSessionRegistry;

    public void send(Client client, String topic, String destination, Object payload) {
        logInfo(client, topic, destination);
        clientSessionRegistry.getById(client.getId())
                .ifPresent(session -> {
                    send(client, topic, destination, payload, session);
                });
    }

    public void send(Client client, String topic, String destination, Object payload, SessionManager session) {
        synchronized (client) {
            session.reconnectIfNotAlive(false);
            logInfo(client, topic, destination);
            session.send(destination, payload);
        }
    }

    private static void logInfo(Client client, String topic, String destination) {
        log.info("Sending message: client={} topic={} destination={}", client.getId(), topic, destination);
    }
}
