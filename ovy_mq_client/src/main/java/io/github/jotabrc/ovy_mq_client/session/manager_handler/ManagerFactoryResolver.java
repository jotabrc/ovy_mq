package io.github.jotabrc.ovy_mq_client.session.manager_handler;

import io.github.jotabrc.ovy_mq_client.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.session.interfaces.Manager;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@RequiredArgsConstructor
@Component
public class ManagerFactoryResolver {

    private final ObjectProviderFacade objectProviderFacade;

    public List<ScheduledFuture<?>> initialize(Client client, SessionManager sessionManager, ManagerFactory... factories) {
        List<ScheduledFuture<?>> scheduledFutures = new ArrayList<>();
        for (ManagerFactory managerFactory : factories) {
            Manager manager = managerFactory.getAndThen.create(objectProviderFacade, client, sessionManager);
            scheduledFutures.add(manager.execute());
        }
        return scheduledFutures;
    }
}
