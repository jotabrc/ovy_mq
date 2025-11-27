package io.github.jotabrc.ovy_mq_client.component;

import io.github.jotabrc.ovy_mq_client.component.session.stomp.manager.HealthCheckManager;
import io.github.jotabrc.ovy_mq_client.component.session.stomp.manager.ListenerPollManager;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ObjectProviderFacade {

    private final ObjectProvider<DefinitionMap> definitionMapProvider;
    private final ObjectProvider<HealthCheckManager> healthCheckManagerObjectProvider;
    private final ObjectProvider<ListenerPollManager> listenerPollManagerObjectProvider;

    public DefinitionMap getDefinitionMap() {
        return definitionMapProvider.getObject();
    }

    public HealthCheckManager getHealthCheckManager() {
        return healthCheckManagerObjectProvider.getObject();
    }

    public ListenerPollManager getListenerPollManager() {
        return listenerPollManagerObjectProvider.getObject();
    }
}
