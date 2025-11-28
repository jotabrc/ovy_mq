package io.github.jotabrc.ovy_mq_client.component.session.stomp.manager;

import io.github.jotabrc.ovy_mq_client.component.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
public class ManagerHandler {

    private final ObjectProviderFacade objectProviderFacade;
    private final Map<String, Set<AbstractManager>> managers = new HashMap<>();

    public void initialize(Client client, SessionManager sessionManager, ManagerFactory... factories) {
        for (ManagerFactory managerFactory : factories) {
            AbstractManager manager = managerFactory.getAndThen.create(objectProviderFacade, client, sessionManager);
            manager.execute();
            managers.compute(client.getId(), (key, set) -> {
                if (isNull(set)) set = new HashSet<>();
                set.add(manager);
                return set;
            });
        }
    }

    public void destroy(String clientId) {
        Optional.ofNullable(managers.remove(clientId))
                .ifPresent(managers -> managers.forEach(AbstractManager::destroy));
    }
}
