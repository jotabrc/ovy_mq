package io.github.jotabrc.ovy_mq_client.component.session.stomp.manager;

import io.github.jotabrc.ovy_mq_client.component.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.util.AbstractTriFunctionFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ManagerFactory {

    HEALTH_CHECK(ObjectProviderFacade::getHealthCheckManager),
    LISTENER_POLL(ObjectProviderFacade::getListenerPollManager);

    public final AbstractTriFunctionFactory<ObjectProviderFacade, Client, SessionManager, AbstractManager> getAndThen;
}
