package io.github.jotabrc.ovy_mq_client.session.client.impl.manager_handler;

import io.github.jotabrc.ovy_mq_client.session.client.interfaces.Manager;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientInitializer;
import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientState;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class ManagerFactoryResolver {

    private final Map<ManagerFactory, Manager<?, ?, ?>> factories = new HashMap<>();

    @Autowired
    public ManagerFactoryResolver(List<Manager<?, ?, ?>> factories) {
        factories.forEach(manager -> this.factories.putIfAbsent(manager.factory(), manager));
    }

    public <T, U, V> List<ScheduledFuture<?>> initialize(ClientInitializer<T, U, V> clientInitializer,
                                                         ClientState<T, U, V> clientState,
                                                         Client client,
                                                         List<ManagerFactory> factories) {
        List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();
        for (ManagerFactory managerFactory : factories) {
            Manager<T, U, V> manager = (Manager<T, U, V>) this.factories.get(managerFactory);
            scheduledFutures.add(manager.execute(client, clientState, clientInitializer));
        }

        return scheduledFutures;
    }
}
