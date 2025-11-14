package io.github.jotabrc.ovy_mq_client.security;

import io.github.jotabrc.ovy_mq_client.config.CredentialConfig;
import io.github.jotabrc.ovy_mq_client.security.interfaces.SecurityResolver;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class BasicSecurityResolver implements SecurityResolver {

    private final CredentialConfig credentialConfig;

    @Override
    public Map<String, List<String>> create(String clientType) {
        var headers = new HashMap<>(createAuthorization());
        headers.putAll(createRoles(clientType));
        return headers;
    }

    private Map<String, List<String>> createAuthorization() {
        String basic = "Basic " + Base64.getEncoder().encodeToString((credentialConfig.getBcrypt()).getBytes(StandardCharsets.UTF_8));
        return Map.of(Key.HEADER_AUTHORIZATION, List.of(basic));
    }

    private Map<String, List<String>> createRoles(String clientType) {
        return Map.of(Key.HEADER_ROLES, List.of(clientType));
    }
}
