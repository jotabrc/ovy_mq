package io.github.jotabrc.ovy_mq_core.exception;

public class OvyException extends RuntimeException {
    public OvyException(String message) {
        super(message);
    }

    public static class SecurityFilterFailure extends OvyException {

        public SecurityFilterFailure(String message) {
            super(message);
        }
    }

    public static class ListenerExecution extends OvyException {

        public ListenerExecution(String message) {
            super(message);
        }
    }

    public static class ListenerInvocationExecution extends OvyException {

        public ListenerInvocationExecution(String message) {
            super(message);
        }
    }

    public static class NotFound extends OvyException {

        public NotFound(String message) {
            super(message);
        }
    }

    public static class AuthorizationDenied extends OvyException {

        public AuthorizationDenied(String message) {
            super(message);
        }
    }

    public static class ConfigurationError extends OvyException {

        public ConfigurationError(String message) {
            super(message);
        }
    }
}
