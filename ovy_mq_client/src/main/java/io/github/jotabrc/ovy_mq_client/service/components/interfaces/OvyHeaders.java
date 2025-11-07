package io.github.jotabrc.ovy_mq_client.service.components.interfaces;

public interface OvyHeaders<T> {

    T createDefault(String destination, String topic);
    Class<?> supports();
}
