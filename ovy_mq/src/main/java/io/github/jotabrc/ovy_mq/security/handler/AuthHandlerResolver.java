package io.github.jotabrc.ovy_mq.security.handler;

import io.github.jotabrc.ovy_mq_core.chain.ChainType;
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

    private final Map<ChainType, AuthHandler> handlers = new HashMap<>();

    @Autowired
    public AuthHandlerResolver(List<AuthHandler> handlers) {
        handlers.forEach(authHandler -> this.handlers.put(authHandler.supports(), authHandler));
    }

    public Optional<AuthHandler> get(ChainType type) {
        return Optional.ofNullable(this.handlers.get(type));
    }
}
