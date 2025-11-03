package io.github.jotabrc.ovy_mq_client.service;

import io.github.jotabrc.ovy_mq_client.domain.factory.StompHeaderFactory;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientSessionInitializerHandler;
import io.github.jotabrc.ovy_mq_client.service.registry.ClientSessionRegistryProvider;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageSender {

    private final ClientSessionInitializerHandler clientSessionInitializerHandler;
    private final ClientSessionRegistryProvider clientSessionRegistryProvider;

    public void send(Client client, String topic, String destination, Object payload) {
        synchronized (client.getId()) {
            logInfo(client, topic, destination);
            clientSessionRegistryProvider.getById(client.getId())
                    .ifPresent(session -> {
                        if (!session.isConnected()) clientSessionInitializerHandler.initialize(client);
                        send(client, topic, destination, payload, session);
                    });
        }
    }

    public void send(Client client, String topic, String destination, Object payload, StompSession session) {
        synchronized (client.getId()) {
            logInfo(client, topic, destination);
            session.send(StompHeaderFactory.get(topic, destination), payload);
        }
    }

    private static void logInfo(Client client, String topic, String destination) {
        log.info("Sending message: client={} topic={} destination={}", client.getId(), topic, destination);
    }
}
