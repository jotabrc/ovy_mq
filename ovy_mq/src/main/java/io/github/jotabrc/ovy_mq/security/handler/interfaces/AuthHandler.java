package io.github.jotabrc.ovy_mq.security.handler.interfaces;

import io.github.jotabrc.ovy_mq.security.SecurityChainType;

import java.util.Map;

public interface AuthHandler {

    String retrieveCredentials(String basic);
    boolean hasCredentials(String credential);
    boolean validate(String password);
    Map<String, Object> createAuthorizationHeader();
    SecurityChainType supports();
}
