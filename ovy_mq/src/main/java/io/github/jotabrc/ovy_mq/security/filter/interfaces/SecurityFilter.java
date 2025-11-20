package io.github.jotabrc.ovy_mq.security.filter.interfaces;

import jakarta.servlet.*;

import java.io.IOException;

public interface SecurityFilter extends Filter {

    @Override
    void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException;
}
