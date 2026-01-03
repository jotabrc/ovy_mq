package io.github.jotabrc.ovy_mq_client.session.manager_handler;

import io.github.jotabrc.ovy_mq_client.session.interfaces.Manager;
import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;

import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public abstract class AbstractManager implements Manager {

    protected SessionManager sessionManager;
    protected Client client;
    protected ScheduledFuture<?> scheduledFuture;

    @Override
    public void destroy() {
        if (nonNull(scheduledFuture) && !scheduledFuture.isDone() && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
    }

    public void setSessionManager(SessionManager sessionManager) {
        if (isNull(this.sessionManager)) {
            this.sessionManager = sessionManager;
        }
    }

    public void setClient(Client client) {
        if (isNull(this.client)) {
            this.client = client;
        }
    }
}