package io.github.jotabrc.ovy_mq_client.service.registry.interfaces;

import io.github.jotabrc.ovy_mq_client.domain.Client;

import java.util.List;

public interface ClientRegistry {

    void save(Client client);
    Client getByClientIdOrThrow(String sessionId);
    List<Client> getAllAvailableClients();
    List<Client> getAllClients();
}
