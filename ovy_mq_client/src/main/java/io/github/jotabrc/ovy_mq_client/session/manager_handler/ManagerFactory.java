package io.github.jotabrc.ovy_mq_client.session.manager_handler;

import io.github.jotabrc.ovy_mq_client.facade.ObjectProviderFacade;
import io.github.jotabrc.ovy_mq_client.session.interfaces.Manager;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.util.AbstractTriFunctionFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ManagerFactory {

    HEALTH_CHECK(ObjectProviderFacade::getHealthCheckManager),
    LISTENER_POLL(ObjectProviderFacade::getListenerPollManager);

    public final AbstractTriFunctionFactory<ObjectProviderFacade, Client, SessionManager, Manager> getAndThen;
}
