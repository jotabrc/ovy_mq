package io.github.jotabrc.ovy_mq.security.filter;

import io.github.jotabrc.ovy_mq.security.filter.interfaces.SecurityChain;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;

import static java.util.Objects.nonNull;

public abstract class AbstractSecurityChain implements SecurityChain {

    protected SecurityChain next;

    @Override
    public SecurityChain setNext(SecurityChain next) {
        this.next = next;
        return next;
    }

    @Override
    public DefinitionMap handleNext(DefinitionMap definition) {
        return nonNull(this.next)
                ? this.next.handle(definition)
                : definition;
    }
}
