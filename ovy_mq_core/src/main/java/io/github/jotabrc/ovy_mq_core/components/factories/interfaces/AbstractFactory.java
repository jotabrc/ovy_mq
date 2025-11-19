package io.github.jotabrc.ovy_mq_core.components.factories.interfaces;

import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;

public interface AbstractFactory<R> {

    R create(DefinitionMap definition);
    Class<R> supports();
}