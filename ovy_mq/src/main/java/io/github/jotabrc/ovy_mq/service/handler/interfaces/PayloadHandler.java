package io.github.jotabrc.ovy_mq.service.handler.interfaces;

import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcherCommand;

public interface PayloadHandler<T> {

    void handle(T t);
    Class<T> supports();
    PayloadDispatcherCommand command();
}
