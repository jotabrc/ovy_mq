package io.github.jotabrc.ovy_mq_client.messaging.message;

import io.github.jotabrc.ovy_mq_client.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientMessageDispatcher {

    private final SessionRegistry sessionRegistry;

    public <T, U, V> ClientAdapter<T, U, V> send(Client client, String destination, Object payload) {
        return sessionRegistry.getById(client.getId())
                .map(clientAdapter -> this.send(clientAdapter, destination, payload))
                .orElseThrow(() -> new OvyException.NotFound("Session for client=%s not found".formatted(client.getId())));
    }

    public <T, U, V> ClientAdapter<T, U, V> send(ClientAdapter<T, U, V> clientAdapter, String destination, Object payload) {
        Client client = clientAdapter.getClientHelper().getClient();
        try {
            if (clientAdapter.getClientState().isConnected()) {
                log.info("Sending message: type={} client={} topic={}", destination, client.getId(), client.getTopic());
                clientAdapter.getClientMessageSender().send(destination, payload);
            }
        } catch (Exception e) {
            throw new OvyException.MessageDispatcher("Error sending message type=%s client=%s topic=%s: %s"
                    .formatted(destination, client.getId(), client.getTopic(), e.getMessage()));
        }
        return  clientAdapter;
    }
}
