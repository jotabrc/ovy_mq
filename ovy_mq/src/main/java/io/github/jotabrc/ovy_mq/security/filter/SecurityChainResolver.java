package io.github.jotabrc.ovy_mq.security.filter;

import io.github.jotabrc.ovy_mq.security.SecurityChainType;
import io.github.jotabrc.ovy_mq.security.filter.interfaces.SecurityChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@Component
public class SecurityChainResolver {

    private final Map<SecurityChainType, SecurityChain> strategies = new HashMap<>();

    @Autowired
    public SecurityChainResolver(List<SecurityChain> strategies) {
        strategies.forEach(securityChain -> this.strategies.put(securityChain.type(), securityChain));
    }

    public Optional<SecurityChain> getByAuth(String auth) {
        if (isNull(auth) || auth.isBlank()) return Optional.empty();
        if (auth.trim().toLowerCase().startsWith("basic "))
            return getByType(SecurityChainType.AUTH_BASE64);

        return Optional.empty();
    }

    public Optional<SecurityChain> getByType(SecurityChainType type) {
        return Optional.ofNullable(this.strategies.get(type));
    }
}
