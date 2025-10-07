package io.github.jotabrc.ovy_mq.service.handler.interfaces;

import io.github.jotabrc.ovy_mq.domain.Client;

import java.util.List;

public interface ClientRegistryHandler extends AbstractHandler {

    void updateClientList(Client client);
    void remove(String clientId);
    Client findClientById(String clientId);
    Client findLeastRecentlyUsedClientByTopic(String topic);
    List<Client> findOneAvailableClientPerTopic();
    List<Client> findAllAvailableClients();
    Integer isThereAnyAvailableClientForTopic(String topic);
}