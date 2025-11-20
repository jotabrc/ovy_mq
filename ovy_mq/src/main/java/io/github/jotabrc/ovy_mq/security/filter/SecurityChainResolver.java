package io.github.jotabrc.ovy_mq.security.filter;

import io.github.jotabrc.ovy_mq.security.SecurityChainType;
import io.github.jotabrc.ovy_mq.security.filter.interfaces.SecurityChain;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class SecurityChainResolver {

    private final Map<SecurityChainType, SecurityChain> strategies = new HashMap<>();

    @Autowired
    public SecurityChainResolver(List<SecurityChain> strategies) {
        strategies.forEach(securityChain -> this.strategies.put(securityChain.type(), securityChain));
    }

    public Optional<SecurityChain> getByAuth(String auth) {
        SecurityChainType type = SecurityChainType.UNSUPPORTED;
        if (Base64.isBase64(auth)) type = SecurityChainType.AUTH_BASE64;
        return Optional.ofNullable(this.strategies.get(type));
    }

    public Optional<SecurityChain> getByType(SecurityChainType type) {
        return Optional.ofNullable(this.strategies.get(type));
    }
}
