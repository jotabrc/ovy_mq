package io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces;

import io.github.jotabrc.ovy_mq_client.session.client.interfaces.ClientMessageSender;

public interface ClientMessageSenderAware<T, U, V> {

    void setClientMessageSender(ClientMessageSender<T, U, V> clientMessageSender);
}
