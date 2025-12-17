package io.github.jotabrc.ovy_mq_client.component.message;

import io.github.jotabrc.ovy_mq_client.component.session.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageDispatcher {

    private final SessionRegistry sessionRegistry;

    public void send(Client client, String topic, String destination, Object payload) {
        sessionRegistry.getById(client.getId())
                .ifPresent(session -> send(client, topic, destination, payload, session));
    }

    public void send(Client client, String topic, String destination, Object payload, SessionManager session) {
        try {
            Optional.ofNullable(session)
                    .filter(SessionManager::isConnected)
                    .ifPresent(sessionManager -> {
                        logInfo(client, topic, destination);
                        session.send(destination, payload);
                    });
        } catch (Exception e) {
            throw new OvyException.MessageDispatcher("Error sending message type=%s client=%s topic=%s: %s"
                    .formatted(destination, client.getId(), topic, e.getMessage()));
        }
    }

    private static void logInfo(Client client, String topic, String destination) {
        log.info("Sending message: type={} client={} topic={}", destination, client.getId(), topic);
    }
}
