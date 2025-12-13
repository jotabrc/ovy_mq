package io.github.jotabrc.ovy_mq.security.filter.chain;

import io.github.jotabrc.ovy_mq_core.chain.ChainType;
import io.github.jotabrc.ovy_mq_core.chain.AbstractChain;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
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
        String role = definition.extract(Key.HEADER_ROLE, String.class);
        if (isNull(role) || role.isBlank()) {
            role = definition.extract(Key.HEADER_CLIENT_TYPE, String.class);
        }
        definition.add(Key.FILTER_ROLES, new ArrayList<>(List.of(role)));
        return handleNext(definition);
    }

    @Override
    public ChainType type() {
        return ChainType.ROLES_IDENTIFIER;
    }
}
