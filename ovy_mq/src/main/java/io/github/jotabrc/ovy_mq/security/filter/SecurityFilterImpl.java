package io.github.jotabrc.ovy_mq.security.filter;

import io.github.jotabrc.ovy_mq.security.filter.interfaces.SecurityChain;
import io.github.jotabrc.ovy_mq.security.filter.interfaces.SecurityFilter;
import io.github.jotabrc.ovy_mq_core.components.interfaces.DefinitionMap;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
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

    private final SecurityChainResolver securityChainResolver;
    private final ObjectProvider<DefinitionMap> definitionProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("Authentication request received");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String auth = req.getHeader(Key.HEADER_AUTHORIZATION);

        if (nonNull(auth) && !auth.isBlank()) {
            SecurityChain securityChain = securityChainResolver.getByAuth(auth)
                    .orElseThrow(() -> new OvyException.SecurityFilterFailure("Security handler not found"));

            DefinitionMap definition = definitionProvider.getObject()
                    .add(Key.HEADER_AUTHORIZATION, auth);
            securityChain.handle(definition);

            String clientId = req.getHeader(Key.HEADER_CLIENT_ID);
            String topic = req.getHeader(Key.HEADER_TOPIC);
            String clientType = req.getHeader(Key.HEADER_CLIENT_TYPE);
            if (nonNull(topic) && !topic.isBlank()) {

                servletRequest.setAttribute(Key.HEADER_CLIENT_ID, clientId);
                servletRequest.setAttribute(Key.HEADER_TOPIC, topic);
                servletRequest.setAttribute(Key.HEADER_CLIENT_TYPE, clientType);
                try {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                } catch (IOException | ServletException e) {
                    throw new OvyException.SecurityFilterFailure("Error while processing authentication: %s. Message: %s".formatted(servletRequest.getRequestId(), e.getMessage()));
                }
            }
        }

        throw new OvyException.AuthorizationDenied("Authorization denied");
    }
}
