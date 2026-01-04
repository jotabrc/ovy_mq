package io.github.jotabrc.ovy_mq_client.session.interfaces;

public interface SessionMessageSender {

    void send(String destination, Object payload);
}
