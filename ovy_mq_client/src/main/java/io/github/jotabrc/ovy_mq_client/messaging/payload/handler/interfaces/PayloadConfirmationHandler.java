package io.github.jotabrc.ovy_mq_client.messaging.payload.handler.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.client.Client;

public interface PayloadConfirmationHandler<T> {

    void acknowledge(Client client, T payload);

    Class<?> supports();
}
