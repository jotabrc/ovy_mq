package io.github.jotabrc.ovy_mq_client.producer.interfaces;

public interface OvyProducer {

    void send(String topic, Object payload);
}
