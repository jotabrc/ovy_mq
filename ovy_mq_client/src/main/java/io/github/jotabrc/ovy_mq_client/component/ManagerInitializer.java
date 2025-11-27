package io.github.jotabrc.ovy_mq_client.component;

import io.github.jotabrc.ovy_mq_client.component.session.stomp.manager.HealthCheckManager;
import io.github.jotabrc.ovy_mq_client.component.session.stomp.manager.ListenerPollManager;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ManagerInitializer {

    private final ObjectProviderFacade objectProviderFacade;

    public void initialize(SessionManager session, Client client) {
        initializeHealthCheckManager(session, client);
        initializeListenerPollManager(session, client);
    }

    private void initializeHealthCheckManager(SessionManager session, Client client) {
        HealthCheckManager healthCheckManager = objectProviderFacade.getHealthCheckManager();
        healthCheckManager.setSession(session);
        healthCheckManager.setClient(client);
        healthCheckManager.execute();
    }

    private void initializeListenerPollManager(SessionManager session, Client client) {
        ListenerPollManager listenerPollManager = objectProviderFacade.getListenerPollManager();
        listenerPollManager.setSession(session);
        listenerPollManager.setClient(client);
        listenerPollManager.execute();
    }
}
