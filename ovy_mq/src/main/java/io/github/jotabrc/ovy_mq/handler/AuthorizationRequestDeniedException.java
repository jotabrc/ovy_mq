package io.github.jotabrc.ovy_mq.handler;

public class AuthorizationRequestDeniedException extends RuntimeException {
    public AuthorizationRequestDeniedException(String message) {
        super(message);
    }
}
