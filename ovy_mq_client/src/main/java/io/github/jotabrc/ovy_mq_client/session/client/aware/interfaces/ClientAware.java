package io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.client.Client;

public interface ClientAware {

    void setClient(Client client);
}
