package io.github.jotabrc.ovy_mq_client.component.producer.interfaces;

import io.github.jotabrc.ovy_mq_client.component.session.interfaces.SessionManager;

public interface ProducerTemplate {

    void setSessionManager(SessionManager sessionManager);
    void send(String topic, Object payload);
}
