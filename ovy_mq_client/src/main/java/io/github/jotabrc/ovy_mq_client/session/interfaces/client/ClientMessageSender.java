package io.github.jotabrc.ovy_mq_client.session.interfaces.client;

import io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware.ClientHelperAware;
import io.github.jotabrc.ovy_mq_client.session.interfaces.client.aware.ClientStateAware;

public interface ClientMessageSender<T, U, V> extends ClientHelperAware<T>, ClientStateAware<T, U, V> {

    void send(String destination, Object payload);
}
