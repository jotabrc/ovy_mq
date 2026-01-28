package io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.ClientMessageSender;

public interface ClientMessageSenderAware<T, U, V> {

    void setClientMessageSender(ClientMessageSender<T, U, V> clientMessageSender);
}
