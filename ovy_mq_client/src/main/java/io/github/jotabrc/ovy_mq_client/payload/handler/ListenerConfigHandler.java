package io.github.jotabrc.ovy_mq_client.payload.handler;

import io.github.jotabrc.ovy_mq_client.payload.handler.interfaces.PayloadHandler;
import io.github.jotabrc.ovy_mq_client.resource.ShutdownClientComponent;
import io.github.jotabrc.ovy_mq_client.session.initialize.SessionInitializer;
import io.github.jotabrc.ovy_mq_client.session.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_client.session.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_core.components.LockProcessor;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.util.Subscribe;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ListenerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class ListenerConfigHandler implements PayloadHandler<ListenerConfig> {

    private final LockProcessor lockProcessor;
    private final ClientRegistry clientRegistry;
    private final SessionRegistry sessionRegistry;
    private final ShutdownClientComponent shutdownClientComponent;
    private final ObjectProvider<DefinitionMap> definitionProvider;
    private final SessionInitializer sessionInitializer;

    @Override
    public void handle(Client client, ListenerConfig payload, StompHeaders headers) {
        Callable<Void> callable = () -> {
            List<Client> clients = clientRegistry.getClientsByTopic(payload.getTopic());

            if (nonNull(clients) && !clients.isEmpty()) {
                if (Objects.equals(0, payload.getReplica().getQuantity())) return null;
                int replicas = payload.getReplica().getQuantity() - clients.size();
                if (payload.getReplica().getQuantity() > clients.size()) {
                    scaleUp(replicas, payload, clients.getFirst(), clients);
                } else {
                    scaleDown(replicas, clients);
                }

                clients.forEach(c -> c.setConfig(payload));
            }
            return null;
        };
        lockProcessor.getLockAndExecute(callable, OvyMqConstants.LOCK_KEY_VALUE.apply(payload.getTopic()), null, null);
    }

    private void scaleUp(int replicas, ListenerConfig listenerConfig, Client data, List<Client> clients) {
        while (replicas > 0) {
            Client client = Client.builder()
                    .id(UUID.randomUUID().toString())
                    .topic(data.getTopic())
                    .config(listenerConfig)
                    .beanName(data.getBeanName())
                    .method(data.getMethod())
                    .type(data.getType())
                    .build();
            DefinitionMap sessionDefinition = definitionProvider.getObject()
                    .add(OvyMqConstants.CLIENT_OBJECT, client)
                    .add(OvyMqConstants.SUBSCRIPTIONS, Subscribe.CONSUMER_SUBSCRIPTION.apply(client.getTopic()));
            sessionInitializer.createSessionAndConnect(client, sessionDefinition);
            clientRegistry.save(client);
            clients.add(client);
            --replicas;
        }
    }

    private void scaleDown(int replicas, List<Client> clients) {
        while (replicas < 0) {
            Client clientToRemove = clients.removeLast();
            sessionRegistry.getById(clientToRemove.getId()).ifPresent(sessionManager ->
                    shutdownClientComponent.stopThis(sessionManager, null));
            ++replicas;
        }
    }

    @Override
    public Class<ListenerConfig> supports() {
        return ListenerConfig.class;
    }
}
