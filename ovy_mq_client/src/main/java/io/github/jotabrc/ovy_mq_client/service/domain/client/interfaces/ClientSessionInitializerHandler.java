package io.github.jotabrc.ovy_mq_client.service.domain.client.interfaces;

public interface ClientSessionInitializerHandler extends AbstractHandler {

    void initializeSession(String topic);
}
