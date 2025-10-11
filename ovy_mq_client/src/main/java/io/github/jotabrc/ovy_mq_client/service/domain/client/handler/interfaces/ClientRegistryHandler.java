package io.github.jotabrc.ovy_mq_client.service.domain.client.handler.interfaces;

import io.github.jotabrc.ovy_mq_client.domain.Client;

import java.util.List;

public interface ClientRegistryHandler extends AbstractHandler {

    void save(Client client);
    Client getByClientIdOrThrow(String sessionId);
    List<Client> getAllAvailableClients();
}
