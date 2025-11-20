package io.github.jotabrc.ovy_mq.security.filter.interfaces;

import io.github.jotabrc.ovy_mq.security.SecurityChainType;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;

public interface SecurityChain {

    SecurityChain setNext(SecurityChain next);
    DefinitionMap handle(DefinitionMap definition);
    DefinitionMap handleNext(DefinitionMap definition);
    SecurityChainType type();
}
