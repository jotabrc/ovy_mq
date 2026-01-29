package io.github.jotabrc.ovy_mq_client.facade;

import io.github.jotabrc.ovy_mq_core.components.util.interfaces.DefinitionMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ObjectProviderFacade {

    private final ObjectProvider<DefinitionMap> definitionMapProvider;

    public DefinitionMap getDefinitionMap() {
        return definitionMapProvider.getObject();
    }
}
