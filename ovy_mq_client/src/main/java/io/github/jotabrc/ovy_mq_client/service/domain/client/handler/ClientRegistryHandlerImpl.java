package io.github.jotabrc.ovy_mq_client.service.domain.client.handler;

import io.github.jotabrc.ovy_mq_client.domain.Client;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.handler.MessageProcessingFailureException;
import io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces.ClientRegistryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientRegistryHandlerImpl implements ClientRegistryHandler {

    private Map<String, Client> clients = new ConcurrentHashMap<>();

    @Override
    public void save(Client client) {
        if (nonNull(client)
                && nonNull(client.getTopic())
                && nonNull(client.getClientSession())) {
            clients.putIfAbsent(client.getTopic(), client);
        } else {
            log.error("Error in client registration, a valid topic and active session must be provided");
        }
    }

    @Override
    public void executeListener(String topic, MessagePayload messagePayload) {
        try {
            clients.get(topic).getMethod().invoke(messagePayload);
            //TODO handle failure/success
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MessageProcessingFailureException(topic, e.getMessage());
        }
    }

    @Override
    public void requestMessagesForAllAvailableClients() {
        clients.values()
                .stream()
                .filter(Client::getIsAvailable)
                .filter(client -> client.getClientSession().getSession().isConnected())
                .takeWhile(Objects::nonNull)
                .forEach(client -> client.getClientSession().getSession().send("/request/message", new MessagePayload()));
    }
}
