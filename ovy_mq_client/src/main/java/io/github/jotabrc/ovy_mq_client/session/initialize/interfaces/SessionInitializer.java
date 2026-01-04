package io.github.jotabrc.ovy_mq_client.session.initialize.interfaces;

import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;

import java.util.Optional;

public interface SessionInitializer {

    Optional<SessionManager> createSessionAndConnect(Client client, DefinitionMap sessionDefinition);
    SessionType supports();
}