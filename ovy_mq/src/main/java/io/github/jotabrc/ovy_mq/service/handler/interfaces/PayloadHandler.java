package io.github.jotabrc.ovy_mq.service.handler.interfaces;

import io.github.jotabrc.ovy_mq.service.handler.PayloadHandlerCommand;

public interface PayloadHandler<T> {

    void handle(T t);
    Class<T> supports();
    PayloadHandlerCommand command();
}
