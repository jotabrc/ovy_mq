package io.github.jotabrc.ovy_mq_client.security;

import io.github.jotabrc.ovy_mq_client.security.interfaces.SecurityResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultSecurityProvider {

    private final SecurityResolver basicSecurityResolver;

    public Map<String, List<String>> create(String clientType) {
        return basicSecurityResolver.create(clientType);
    }
}
