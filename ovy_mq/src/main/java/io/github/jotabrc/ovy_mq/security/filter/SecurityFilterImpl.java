package io.github.jotabrc.ovy_mq.security.filter;

import io.github.jotabrc.ovy_mq.security.SecurityChainType;
import io.github.jotabrc.ovy_mq.security.filter.interfaces.SecurityChain;
import io.github.jotabrc.ovy_mq.security.filter.interfaces.SecurityFilter;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import io.github.jotabrc.ovy_mq_core.domain.ClientType;
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
import java.util.Objects;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class SecurityFilterImpl implements SecurityFilter {

    private final SecurityChainResolver securityChainResolver;
    private final ObjectProvider<DefinitionMap> definitionProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("Authentication request received");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String auth = req.getHeader(Key.HEADER_AUTHORIZATION);

        if (nonNull(auth) && !auth.isBlank()) {
            SecurityChain securityChain = securityChainResolver.getByAuth(auth)
                    .orElseThrow(() -> new OvyException.SecurityFilterFailure("Authentication handler not found"));

            // todo
            //  first chain should initialize the DefinitionMap extracting headers from the request
            // and return the attributes
            securityChain.setNext(securityChainResolver.getByType(SecurityChainType.SUBJECT_IDENTIFIER)
                            .orElseThrow(() -> new OvyException.SecurityFilterFailure("Subject handler not found")))
                    .setNext(securityChainResolver.getByType(SecurityChainType.ROLES_IDENTIFIER)
                            .orElseThrow(() -> new OvyException.SecurityFilterFailure("Roles handler not found")))
                    .setNext(securityChainResolver.getByType(SecurityChainType.AUTHENTICATION_CREATOR)
                            .orElseThrow(() -> new OvyException.SecurityFilterFailure("Authentication creator handler not found")));

            String clientId = req.getHeader(Key.HEADER_CLIENT_ID);
            String topic = req.getHeader(Key.HEADER_TOPIC);
            String clientType = req.getHeader(Key.HEADER_CLIENT_TYPE);

            if (nonNull(topic) && !topic.isBlank()
                    && nonNull(clientType) && !clientType.isBlank()
                    && ((nonNull(clientId) && !clientId.isBlank()) || Objects.equals(ClientType.CONFIGURER.name(), clientType))) {

                DefinitionMap definition = definitionProvider.getObject()
                        .add(Key.HEADER_AUTHORIZATION, auth)
                        .add(Key.HEADER_CLIENT_ID, clientId)
                        .add(Key.HEADER_TOPIC, topic)
                        .add(Key.HEADER_CLIENT_TYPE, clientType);
                securityChain.handle(definition);

                servletRequest.setAttribute(Key.HEADER_CLIENT_ID, clientId);
                servletRequest.setAttribute(Key.HEADER_TOPIC, topic);
                servletRequest.setAttribute(Key.HEADER_CLIENT_TYPE, clientType);
                try {
                    filterChain.doFilter(servletRequest, servletResponse);
                    log.info("Authenticated");
                    return;
                } catch (IOException | ServletException e) {
                    throw new OvyException.SecurityFilterFailure("Error while processing authentication: %s. Message: %s".formatted(servletRequest.getRequestId(), e.getMessage()));
                }
            }
        }

        throw new OvyException.AuthorizationDenied("Authorization denied");
    }
}
