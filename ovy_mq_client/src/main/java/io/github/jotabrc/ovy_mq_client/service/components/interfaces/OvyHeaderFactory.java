package io.github.jotabrc.ovy_mq_client.service.components.interfaces;

public interface OvyHeaderFactory<R> {

    R createDefault(String destination, String topic);
    Class<R> supports();
}
