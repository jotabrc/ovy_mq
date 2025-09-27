package io.github.jotabrc.ovy_mq.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class RequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String> wrapperHeaders = new HashMap<>();

    public RequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String key, String value) {
        if (isNull(key) || key.isBlank() || isNull(value) || value.isBlank()) return;
        wrapperHeaders.put(key, value);
    }

    public void addAllHeaders(HttpServletRequest req) {
        req.getHeaderNames().asIterator().forEachRemaining(name -> wrapperHeaders.putIfAbsent(name, super.getHeader(name)));
    }

    @Override
    public String getHeader(String name) {
        String value = wrapperHeaders.get(name);
        return (nonNull(value))
                ? value
                : super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String value = wrapperHeaders.get(name);
        return (nonNull(value))
                ? Collections.enumeration(List.of(value))
                : super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> keys = new HashSet<>(wrapperHeaders.keySet());
        Enumeration<String> original = super.getHeaderNames();
        while (original.hasMoreElements()) {
            keys.add(original.nextElement());
        }
        return Collections.enumeration(keys);
    }
}
