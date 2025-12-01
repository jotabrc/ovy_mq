package io.github.jotabrc.ovy_mq_client.component;

import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_client.component.session.stomp.manager.HealthCheckManager;
import io.github.jotabrc.ovy_mq_client.component.session.stomp.manager.ListenerPollManager;
import io.github.jotabrc.ovy_mq_client.component.session.stomp.manager.ShutdownManager;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ObjectProviderFacade {

    private final ObjectProvider<DefinitionMap> definitionMapProvider;
    private final ObjectProvider<HealthCheckManager> healthCheckManagerObjectProvider;
    private final ObjectProvider<ListenerPollManager> listenerPollManagerObjectProvider;
    private final ObjectProvider<ShutdownManager> sessioManagerLifecycleManagerObjectProvider;

    public DefinitionMap getDefinitionMap() {
        return definitionMapProvider.getObject();
    }

    public HealthCheckManager getHealthCheckManager(Client client, SessionManager sessionManager) {
        HealthCheckManager healthCheckManager = healthCheckManagerObjectProvider.getObject();
        healthCheckManager.setClient(client);
        healthCheckManager.setSession(sessionManager);
        return healthCheckManager;
    }

    public ListenerPollManager getListenerPollManager(Client client, SessionManager sessionManager) {
        ListenerPollManager listenerPollManager = listenerPollManagerObjectProvider.getObject();
        listenerPollManager.setClient(client);
        listenerPollManager.setSession(sessionManager);
        return listenerPollManager;
    }

    public ShutdownManager getSessionManagerDestroyManager(Client client, SessionManager sessionManager) {
        ShutdownManager shutdownManager = sessioManagerLifecycleManagerObjectProvider.getObject();
        shutdownManager.setClient(client);
        shutdownManager.setSessionManager(sessionManager);
        return shutdownManager;
    }
}
