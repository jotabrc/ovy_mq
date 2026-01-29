package io.github.jotabrc.ovy_mq_client.session.initialize.interfaces;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.jotabrc.ovy_mq_client.session.client.impl.SessionType;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientAdapter;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;

import java.util.Optional;

public interface ClientHandlerInitializer {

    <T, U, V> Optional<ClientAdapter<T, U, V>> createAndInitialize(Client client, DefinitionMap sessionDefinition, TypeReference<ClientAdapter<T, U, V>> typeReference);
    SessionType supports();
}