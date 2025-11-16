package io.github.jotabrc.ovy_mq_client.service.components.handler.payload.interfaces;

import io.github.jotabrc.ovy_mq_client.service.components.handler.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.Client;

public interface PayloadConfirmationHandler<T> {

    void acknowledge(SessionManager session,
                     Client client,
                     String destination,
                     T payload);

    Class<?> supports();
}
