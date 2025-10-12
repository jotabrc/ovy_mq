package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@RequiredArgsConstructor
@Component
public class ClientRegistryContextHolder {

    private final ConcurrentHashMap<String, ConcurrentSkipListSet<Client>> clients = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, ConcurrentSkipListSet<Client>> getClients() {
        return clients;
    }
}
