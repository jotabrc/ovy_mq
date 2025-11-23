package io.github.jotabrc.ovy_mq.security.filter;

import io.github.jotabrc.ovy_mq.registry.ConfigClientContextHolder;
import io.github.jotabrc.ovy_mq.security.SecurityChainType;
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
public class RoleSecurityChain extends AbstractSecurityChain {

    private final ConfigClientContextHolder configClientContextHolder;

    @Override
    public DefinitionMap handle(DefinitionMap definition) {
        List<String> roles = definition.extractToList(Key.FILTER_ROLES, String.class);
        if (isNull(roles) || roles.isEmpty()) {
            String clientType = definition.extract(Key.HEADER_CLIENT_TYPE, String.class);
            definition.add(Key.FILTER_ROLES, new ArrayList<>(List.of(clientType)));
        }
        return handleNext(definition);
    }

    @Override
    public SecurityChainType type() {
        return SecurityChainType.ROLES_IDENTIFIER;
    }
}
