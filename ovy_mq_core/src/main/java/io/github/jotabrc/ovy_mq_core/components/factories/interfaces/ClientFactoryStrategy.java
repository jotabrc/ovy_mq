package io.github.jotabrc.ovy_mq_core.components.factories.interfaces;

import io.github.jotabrc.ovy_mq_core.components.util.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.domain.client.Client;
import io.github.jotabrc.ovy_mq_core.domain.client.ClientType;

public interface ClientFactoryStrategy {

    Client create(DefinitionMap definition);
    ClientType supports();
}
