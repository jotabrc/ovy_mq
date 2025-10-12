package io.github.jotabrc.ovy_mq.service.handler.interfaces;

import io.github.jotabrc.ovy_mq.domain.Client;

public interface ClientRegistryRemoveHandler extends ClientRegistryAbstractHandler {

    Client handle(Client client);
}
