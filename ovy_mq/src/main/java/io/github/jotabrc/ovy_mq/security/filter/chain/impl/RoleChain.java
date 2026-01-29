package io.github.jotabrc.ovy_mq.security.filter.chain.impl;

import io.github.jotabrc.ovy_mq_core.chain.ChainType;
import io.github.jotabrc.ovy_mq_core.chain.AbstractChain;
import io.github.jotabrc.ovy_mq_core.components.util.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class RoleChain extends AbstractChain {

    @Override
    public DefinitionMap handle(DefinitionMap definition) {
        String role = definition.extract(OvyMqConstants.ROLES, String.class);
        if (isNull(role) || role.isBlank()) {
            role = definition.extract(OvyMqConstants.CLIENT_TYPE, String.class);
        }
        definition.add(OvyMqConstants.FILTER_ROLES, new ArrayList<>(List.of(role)));
        return handleNext(definition);
    }

    @Override
    public ChainType type() {
        return ChainType.ROLES_IDENTIFIER;
    }
}
