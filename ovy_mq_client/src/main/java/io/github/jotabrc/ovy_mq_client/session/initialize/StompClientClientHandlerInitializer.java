package io.github.jotabrc.ovy_mq_client.session.initialize;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.jotabrc.ovy_mq_client.factory.ClientHandlerFactory;
import io.github.jotabrc.ovy_mq_client.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_client.session.initialize.interfaces.ClientHandlerInitializer;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_client.session.manager_handler.ManagerFactory;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompClientClientHandlerInitializer implements ClientHandlerInitializer {

    private final SessionRegistry sessionRegistry;
    private final ClientHandlerFactory clientHandlerFactory;

    @Override
    public <T, U, V> Optional<ClientAdapter<T, U, V>> createAndInitialize(Client client, DefinitionMap definition, TypeReference<ClientAdapter<T, U, V>> typeReference) {
        log.info("Creating ClientHelper and initializing: client={}", client.getId());
        var clientAdapter = clientHandlerFactory.buildHandlerForStomp(client, definition);
        sessionRegistry.addOrReplace(client.getId(), clientAdapter);
        clientAdapter.getClientHelper().setScheduledFutures(clientAdapter.getClientInitializer().initializeManagers(definition.extractToList(OvyMqConstants.MANAGERS, ManagerFactory.class)));
        clientAdapter.getClientHelper().setConnectionFuture(clientAdapter.getClientInitializer().initializeSession());
        return Optional.of((ClientAdapter<T, U, V>) clientAdapter);
    }

    @Override
    public SessionType supports() {
        return SessionType.STOMP;
    }
}