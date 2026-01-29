package io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces;

import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientHelper;

public interface ClientHelperAware<T> {

    void setClientHelper(ClientHelper<T> clientHelper);
}
