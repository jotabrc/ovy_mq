package io.github.jotabrc.ovy_mq_client.service;

import io.github.jotabrc.ovy_mq_core.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class ClientRegistryProvider {

    private Map<String, Queue<Client>> clients = new ConcurrentHashMap<>();

    public Map<String, Queue<Client>> getClients() {
        return clients;
    }
}

