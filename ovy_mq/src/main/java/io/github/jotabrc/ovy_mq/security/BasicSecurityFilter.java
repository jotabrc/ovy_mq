package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq.handler.AuthorizationRequestDeniedException;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class BasicSecurityFilter implements Filter {

    private final SecurityHandler securityHandler;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("Authentication request received: {}", servletRequest.getRequestId());
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String auth = req.getHeader(Key.HEADER_AUTHORIZATION);
        String topic = req.getHeader(Key.HEADER_TOPIC);

        if (nonNull(auth) && !auth.isBlank() && nonNull(topic) && !topic.isBlank()) {
            String credential = securityHandler.retrieveCredentials(auth);
            if (securityHandler.hasCredentials(credential)) {
                if (securityHandler.validate(credential)) {
                    String clientId = createClientId();
                    log.info("Authentication: authorized-client={}", clientId);

                    servletRequest.setAttribute(Key.HEADER_CLIEND_ID, clientId);
                    servletRequest.setAttribute(Key.HEADER_TOPIC, topic);
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
            }
        }

        log.info("Authentication: denied-request={}", servletRequest.getRequestId());
        throw new AuthorizationRequestDeniedException("Request for authorization is denied");
    }

    private String createClientId() {
        return System.currentTimeMillis() + ":" + UUID.randomUUID();
    }
}
