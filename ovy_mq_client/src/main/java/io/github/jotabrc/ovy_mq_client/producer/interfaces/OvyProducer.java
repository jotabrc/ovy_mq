package io.github.jotabrc.ovy_mq_client.producer.interfaces;

public interface OvyProducer<T, U, V> {

    void send(String topic, Object payload);
}
