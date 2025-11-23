package io.github.jotabrc.ovy_mq.security.filter;

import io.github.jotabrc.ovy_mq.security.SecurityChainType;
import io.github.jotabrc.ovy_mq.security.handler.AuthHandlerResolver;
import io.github.jotabrc.ovy_mq.security.handler.interfaces.AuthHandler;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class BasicSecurityChain extends AbstractSecurityChain {

    private final AuthHandlerResolver authHandlerResolver;

    @Override
    public DefinitionMap handle(DefinitionMap definition) {
        String auth = definition.extract(Key.HEADER_AUTHORIZATION, String.class);

        if (nonNull(auth) && !auth.isBlank()) {
            AuthHandler authHandler = authHandlerResolver.get(SecurityChainType.AUTH_BASE64)
                    .orElseThrow(() -> new OvyException.SecurityFilterFailure("Auth handler not found"));

            String credential = authHandler.retrieveCredentials(auth);
            if (authHandler.hasCredentials(credential) && authHandler.validate(credential)) {
                return handleNext(definition);
            }
        }

        throw new OvyException.AuthorizationDenied("Authorization denied");
    }

    @Override
    public SecurityChainType type() {
        return SecurityChainType.AUTH_BASE64;
    }
}
