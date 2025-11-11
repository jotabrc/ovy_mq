package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionInitializer;
import io.github.jotabrc.ovy_mq_client.service.components.handler.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.service.registry.SessionRegistry;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Getter
@Slf4j
@RequiredArgsConstructor
@Component
public class SessionInitializerImpl implements SessionInitializer {

    private final ObjectProvider<SessionManager> sessionManagerProvider;
    private final SessionRegistry sessionRegistry;



    @Override
    public SessionManager createSessionAndConnect(Client client) {
        SessionManager sessionManager = sessionManagerProvider.getObject();
        sessionManager.setClient(client);
        sessionRegistry.addOrReplace(client.getId(), sessionManager);
        sessionManager.initialize();
        return sessionManager;
    }
}
