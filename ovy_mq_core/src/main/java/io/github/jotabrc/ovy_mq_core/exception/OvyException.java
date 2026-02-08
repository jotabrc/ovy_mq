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

    public static class MessageDispatcher extends OvyException {

        public MessageDispatcher(String message) {
            super(message);
        }
    }

    public static class PartitionExtraction extends OvyException {

        public PartitionExtraction(String message) {
            super(message);
        }
    }

    public static class WriteOperation extends OvyException {

        public WriteOperation(String message, String exceptionMessage, String path) {
            super("%s; path=%s: %s".formatted(message, path, exceptionMessage));
        }
    }

    public static class ReadOperation extends OvyException {

        public ReadOperation(String message, String exceptionMessage, String path) {
            super("%s; path=%s: %s".formatted(message, path, exceptionMessage));
        }
    }

    public static class ReplaceOperation extends OvyException {

        public ReplaceOperation(String message, String exceptionMessage, String path) {
            super("%s; path=%s: %s".formatted(message, path, exceptionMessage));
        }
    }

    public static class OffsetOperation extends OvyException {

        public OffsetOperation(String message, String exceptionMessage, String path) {
            super("%s; path=%s: %s".formatted(message, path, exceptionMessage));
        }
    }

    public static class FileCreation extends OvyException {

        public FileCreation(String message, String exceptionMessage, String path) {
            super("%s; path=%s: %s".formatted(message, path, exceptionMessage));
        }
    }

    public static class DirectoryCreation extends OvyException {

        public DirectoryCreation(String message, String exceptionMessage, String path) {
            super("%s; path=%s: %s".formatted(message, path, exceptionMessage));
        }
    }

    public static class FileDeletion extends OvyException {

        public FileDeletion(String message, String exceptionMessage, String path) {
            super("%s; path=%s: %s".formatted(message, path, exceptionMessage));
        }
    }
}
