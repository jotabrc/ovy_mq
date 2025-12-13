package io.github.jotabrc.ovy_mq_core.chain;

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
public class ChainResolver {

    private final Map<ChainType, BaseChain> strategies = new HashMap<>();

    @Autowired
    public ChainResolver(List<BaseChain> strategies) {
        strategies.forEach(baseChain -> this.strategies.put(baseChain.type(), baseChain));
    }

    public Optional<BaseChain> getByAuth(String auth) {
        if (isNull(auth) || auth.isBlank()) return Optional.empty();
        if (auth.trim().toLowerCase().startsWith("basic "))
            return getByType(ChainType.AUTH_BASE64);

        return Optional.empty();
    }

    public Optional<BaseChain> getByType(ChainType type) {
        return Optional.ofNullable(this.strategies.get(type));
    }
}
