package io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware;

import io.github.jotabrc.ovy_mq_core.domain.client.Client;

public interface ClientAware {

    void setClient(Client client);
}
