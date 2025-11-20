package io.github.jotabrc.ovy_mq.security.handler;

import io.github.jotabrc.ovy_mq.security.SecurityChainType;
import io.github.jotabrc.ovy_mq.security.handler.interfaces.AuthHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuthHandlerResolver {

    private final Map<SecurityChainType, AuthHandler> handlers = new HashMap<>();

    @Autowired
    public AuthHandlerResolver(List<AuthHandler> handlers) {
        handlers.forEach(authHandler -> this.handlers.put(authHandler.supports(), authHandler));
    }

    public Optional<AuthHandler> get(SecurityChainType type) {
        return Optional.ofNullable(this.handlers.get(type));
    }
}
