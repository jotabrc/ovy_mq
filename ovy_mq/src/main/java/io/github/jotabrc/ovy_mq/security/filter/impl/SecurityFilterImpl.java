package io.github.jotabrc.ovy_mq.security.filter.impl;

import io.github.jotabrc.ovy_mq_core.chain.ChainType;
import io.github.jotabrc.ovy_mq_core.chain.ChainResolver;
import io.github.jotabrc.ovy_mq_core.chain.BaseChain;
import io.github.jotabrc.ovy_mq.security.filter.interfaces.SecurityFilter;
import io.github.jotabrc.ovy_mq_core.components.util.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.constants.OvyMqConstants;
import io.github.jotabrc.ovy_mq_core.exception.OvyException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class SecurityFilterImpl implements SecurityFilter {

    private final ChainResolver chainResolver;
    private final ObjectProvider<DefinitionMap> definitionProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("Authentication request received");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String auth = req.getHeader(OvyMqConstants.AUTHORIZATION);

        if (nonNull(auth) && !auth.isBlank()) {
            BaseChain baseChain = chainResolver.getByType(ChainType.DEFINITION_CREATOR)
                    .orElseThrow(() -> new OvyException.SecurityFilterFailure("DefinitionMapImpl handler not found"));

            baseChain.setNext(chainResolver.getByAuth(auth)
                            .orElseThrow(() -> new OvyException.SecurityFilterFailure("Authentication handler not found")))
                    .setNext(chainResolver.getByType(ChainType.SUBJECT_IDENTIFIER)
                            .orElseThrow(() -> new OvyException.SecurityFilterFailure("Subject handler not found")))
                    .setNext(chainResolver.getByType(ChainType.ROLES_IDENTIFIER)
                            .orElseThrow(() -> new OvyException.SecurityFilterFailure("Roles handler not found")))
                    .setNext(chainResolver.getByType(ChainType.AUTHENTICATION_CREATOR)
                            .orElseThrow(() -> new OvyException.SecurityFilterFailure("Authentication creator handler not found")));

            DefinitionMap definition = definitionProvider.getObject().add(OvyMqConstants.FILTER_SERVLET_REQUEST, req);
            definition = baseChain.handle(definition);

            servletRequest.setAttribute(OvyMqConstants.CLIENT_ID, definition.extract(OvyMqConstants.CLIENT_ID, String.class));
            servletRequest.setAttribute(OvyMqConstants.SUBSCRIBED_TOPIC, definition.extract(OvyMqConstants.SUBSCRIBED_TOPIC, String.class));
            servletRequest.setAttribute(OvyMqConstants.CLIENT_TYPE, definition.extract(OvyMqConstants.CLIENT_TYPE, String.class));
            try {
                filterChain.doFilter(servletRequest, servletResponse);
                log.info("Authenticated");
                return;
            } catch (IOException | ServletException e) {
                throw new OvyException.SecurityFilterFailure("Error while processing authentication: %s. Message: %s".formatted(servletRequest.getRequestId(), e.getMessage()));
            }
        }

        throw new OvyException.AuthorizationDenied("Authorization denied");
    }
}
