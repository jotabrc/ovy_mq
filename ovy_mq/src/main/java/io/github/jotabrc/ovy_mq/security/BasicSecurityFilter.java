package io.github.jotabrc.ovy_mq.security;

import io.github.jotabrc.ovy_mq.handler.AuthorizationRequestDeniedException;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
        String clientId = req.getHeader(Key.HEADER_CLIEND_ID);
        String topic = req.getHeader(Key.HEADER_TOPIC);
        String clientType = req.getHeader(Key.HEADER_CLIENT_TYPE);

        if (nonNull(auth) && !auth.isBlank() && nonNull(topic) && !topic.isBlank()) {
            String credential = securityHandler.retrieveCredentials(auth);
            if (securityHandler.hasCredentials(credential)) {
                if (securityHandler.validate(credential)) {
                    log.info("Authorized: client={} topic={} type={}", clientId, topic, clientType);

                    servletRequest.setAttribute(Key.HEADER_CLIEND_ID, clientId);
                    servletRequest.setAttribute(Key.HEADER_TOPIC, topic);
                    servletRequest.setAttribute(Key.HEADER_CLIENT_TYPE, clientType);
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
            }
        }

        log.info("Authentication: denied-request={}", servletRequest.getRequestId());
        throw new AuthorizationRequestDeniedException("Request for authorization is denied");
    }
}
