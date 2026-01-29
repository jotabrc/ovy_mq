package io.github.jotabrc.ovy_mq_client.messaging.payload.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.jotabrc.ovy_mq_client.facade.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.registry.ClientRegistry;
import io.github.jotabrc.ovy_mq_client.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler.stomp_handler.StompClientSessionHandler;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.initialize.impl.SessionInitializerResolver;
import io.github.jotabrc.ovy_mq_client.shutdown.ApplicationShutdownManager;
import io.github.jotabrc.ovy_mq_core.components.LockProcessor;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientExecution;
import io.github.jotabrc.ovy_mq_core.domain.client.ListenerConfig;
import io.github.jotabrc.ovy_mq_core.util.Subscribe;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Component
public class ScaleActionComponent implements ScaleAction {

    private final LockProcessor lockProcessor;
    private final ClientRegistry clientRegistry;
    private final SessionRegistry sessionRegistry;
    private final ApplicationShutdownManager applicationShutdownManager;
    private final ObjectProviderFacade objectProviderFacade;
    private final SessionInitializerResolver sessionInitializerResolver;

    @Override
    public void scale(ListenerConfig listenerConfig) {
        Callable<Void> callable = () -> {
            List<Client> clients = clientRegistry.getClientsByTopic(listenerConfig.getTopic());

            if (nonNull(clients) && !clients.isEmpty()) {

                if (Objects.equals(0, listenerConfig.getReplica().getQuantity())) return null;
                int replicas = listenerConfig.getReplica().getQuantity() - clients.size();

                if (isScaleUp(listenerConfig, clients)) {
                    up(replicas, listenerConfig, clients.getFirst(), clients);
                } else {
                    down(replicas, clients);
                }

                clients.forEach(c -> c.setConfig(listenerConfig));
            }
            return null;
        };
        lockProcessor.getLockAndExecute(callable, OvyMqConstants.LOCK_KEY_VALUE.apply(listenerConfig.getTopic()), null, null);
    }

    private boolean isScaleUp(ListenerConfig listenerConfig, List<Client> clients) {
        return listenerConfig.getReplica().getQuantity() > clients.size();
    }

    @Override
    public void up(int replicas, ListenerConfig listenerConfig, Client data, List<Client> clients) {
        while (replicas > 0) {
            Client client = Client.builder()
                    .id(UUID.randomUUID().toString())
                    .topic(data.getTopic())
                    .config(listenerConfig)
                    .execution(ClientExecution.builder()
                            .beanName(data.getExecution().getBeanName())
                            .method(data.getExecution().getMethod())
                            .build())
                    .type(data.getType())
                    .build();
            DefinitionMap sessionDefinition = objectProviderFacade.getDefinitionMap()
                    .add(OvyMqConstants.CLIENT_OBJECT, client)
                    .add(OvyMqConstants.SUBSCRIPTIONS, Subscribe.CONSUMER_SUBSCRIPTION.apply(client.getTopic()));
            sessionInitializerResolver.get()
                    .ifPresent(sessionInitializer -> sessionInitializer.createAndInitialize(client,
                            sessionDefinition,
                            new TypeReference<ClientAdapter<StompSession, WebSocketHttpHeaders, StompClientSessionHandler>>() {
                            }));
            clientRegistry.save(client);
            --replicas;
        }
    }

    @Override
    public void down(int replicas, List<Client> clients) {
        while (replicas < 0) {
            clients.sort(Comparator.comparing(Client::canDisconnect).reversed()
                    .thenComparing(client -> client.getState().getLastExecution().get()));
            Client clientToRemove = clients.removeFirst();
            sessionRegistry.getById(clientToRemove.getId()).ifPresent(applicationShutdownManager::stopThis);
            ++replicas;
        }
    }
}