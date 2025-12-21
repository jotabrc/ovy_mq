package io.github.jotabrc.ovy_mq_client.payload.handler.interfaces;

import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;

public interface PayloadConfirmationHandler<T> {

    void acknowledge(SessionManager session,
                     Client client,
                     String destination,
                     T payload);

    Class<?> supports();
}
