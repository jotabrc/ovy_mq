package io.github.jotabrc.ovy_mq_client.messaging.message;

import io.github.jotabrc.ovy_mq_client.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;
import io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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
        String type = destination;
        if (payload instanceof OvyAction ovyAction) type = ovyAction.getCommands()
                .stream()
                .map(OvyCommand::name)
                .collect(Collectors.joining(":"));
        try {
            if (clientAdapter.getClientState().isConnected()) {
                log.info("Sending message: type={} client={} topic={}", type, client.getId(), client.getTopic());
                clientAdapter.getClientMessageSender().send(destination, payload);
            }
        } catch (Exception e) {
            throw new OvyException.MessageDispatcher("Error sending message type=%s client=%s topic=%s: %s"
                    .formatted(type, client.getId(), client.getTopic(), e.getMessage()));
        }
        return clientAdapter;
    }
}
