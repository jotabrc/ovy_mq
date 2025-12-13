package io.github.jotabrc.ovy_mq_core.chain;

import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;

import static java.util.Objects.nonNull;

public abstract class AbstractChain implements BaseChain {

    protected BaseChain next;

    @Override
    public BaseChain setNext(BaseChain next) {
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
