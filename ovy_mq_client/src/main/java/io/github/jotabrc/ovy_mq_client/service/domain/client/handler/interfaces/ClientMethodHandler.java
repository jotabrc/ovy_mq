package io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces;

import java.lang.reflect.Method;

public interface ClientMethodHandler extends AbstractHandler {

    void putIfAbsent(String topic, Method method);
    void invoke(String topic, Method method);
}
