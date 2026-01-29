package io.github.jotabrc.ovy_mq_core.chain;

import io.github.jotabrc.ovy_mq_core.components.util.interfaces.DefinitionMap;

public interface BaseChain {

    BaseChain setNext(BaseChain next);
    DefinitionMap handle(DefinitionMap definition);
    DefinitionMap handleNext(DefinitionMap definition);
    ChainType type();
}
