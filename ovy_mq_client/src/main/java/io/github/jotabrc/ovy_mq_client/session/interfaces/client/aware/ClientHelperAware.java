package io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientHelper;

public interface ClientHelperAware<T> {

    void setClientHelper(ClientHelper<T> clientHelper);
}
