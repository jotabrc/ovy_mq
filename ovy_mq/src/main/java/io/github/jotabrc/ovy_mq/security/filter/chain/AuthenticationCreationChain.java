package io.github.jotabrc.ovy_mq.security.filter.chain;

import io.github.jotabrc.ovy_mq_core.chain.ChainType;
import io.github.jotabrc.ovy_mq_core.chain.AbstractChain;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationCreationChain extends AbstractChain {

    private final ObjectProvider<DefinitionMap> definitionProvider;

    @Override
    public DefinitionMap handle(DefinitionMap definition) {
        String subject = definition.extract(Key.FILTER_SUBJECT, String.class);
        List<String> roles = definition.extractToList(Key.FILTER_ROLES, String.class);

        if (nonNull(subject) && !subject.isBlank()
                && nonNull(roles) && !roles.isEmpty()) {
            List<GrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
            Authentication auth = new UsernamePasswordAuthenticationToken(subject, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return handleNext(definition);
        }

        throw new OvyException.AuthorizationDenied("Authorization denied");
    }

    @Override
    public ChainType type() {
        return ChainType.AUTHENTICATION_CREATOR;
    }
}
