package io.github.jotabrc.ovy_mq_client.session.manager_handler;

import io.github.jotabrc.ovy_mq_client.session.interfaces.Manager;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientAdapter;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;

import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public abstract class AbstractManager<T, U, V> implements Manager {

    protected ClientAdapter<T, U, V> clientAdapter;
    protected Client client;
    protected ScheduledFuture<?> scheduledFuture;

    @Override
    public void destroy() {
        if (nonNull(scheduledFuture) && !scheduledFuture.isDone() && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
    }

    public void setClientAdapter(ClientAdapter<T, U, V> clientAdapter) {
        if (isNull(this.clientAdapter)) {
            this.clientAdapter = clientAdapter;
        }
    }

    public void setClient(Client client) {
        if (isNull(this.client)) {
            this.client = client;
        }
    }
}