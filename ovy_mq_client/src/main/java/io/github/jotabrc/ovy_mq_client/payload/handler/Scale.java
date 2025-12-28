package io.github.jotabrc.ovy_mq_client.payload.handler;

import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ListenerConfig;

import java.util.List;

public interface Scale {

    void scale(ListenerConfig listenerConfig);
    void up(int replicas, ListenerConfig listenerConfig, Client data, List<Client> clients);
    void down(int replicas, List<Client> clients);
}