package io.github.jotabrc.ovy_mq_core.security;

import io.github.jotabrc.ovy_mq_core.config.CredentialConfig;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.security.interfaces.SecurityResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class BasicSecurityResolver implements SecurityResolver {

    private final CredentialConfig credentialConfig;

    @Override
    public Map<String, List<String>> create(String clientType) {
        Map<String, List<String>> headers = new HashMap<>();
        addToList(headers, createAuthorization());
        addToList(headers, createClientTypeRole(clientType));
        return headers;
    }

    @Override
    public Map<String, String> createSimple(String clientType) {
        var headers = new HashMap<>(createAuthorization());
        headers.putAll(createClientTypeRole(clientType));
        return headers;
    }

    private Map<String, String> createAuthorization() {
        String basic = "Basic " + Base64.getEncoder().encodeToString((credentialConfig.getBcrypt()).getBytes(StandardCharsets.UTF_8));
        return Map.of(OvyMqConstants.AUTHORIZATION, basic);
    }

    private Map<String, String> createClientTypeRole(String clientType) {
        return Map.of(OvyMqConstants.ROLES, clientType);
    }

    private void addToList(Map<String, List<String>> headers, Map<String, String> map) {
        map.forEach((k, v) -> headers.compute(k, (headerKey, list) -> {
            if (isNull(list)) list = new ArrayList<>();
            list.add(k);
            return list;
        }));
    }
}
