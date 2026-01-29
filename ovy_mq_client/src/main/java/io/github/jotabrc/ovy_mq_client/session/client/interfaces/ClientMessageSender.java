package io.github.jotabrc.ovy_mq_client.session.client.interfaces;

import io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces.ClientHelperAware;
import io.github.jotabrc.ovy_mq_client.session.client.aware.interfaces.ClientStateAware;

public interface ClientMessageSender<T, U, V> extends ClientHelperAware<T>, ClientStateAware<T, U, V> {

    void send(String destination, Object payload);
}
