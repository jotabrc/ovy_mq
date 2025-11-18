package io.github.jotabrc.ovy_mq_client.component.payload.interfaces;

import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;
import io.github.jotabrc.ovy_mq_core.domain.Client;

public interface PayloadConfirmationHandler<T> {

    void acknowledge(SessionManager session,
                     Client client,
                     String destination,
                     T payload);

    Class<?> supports();
}
