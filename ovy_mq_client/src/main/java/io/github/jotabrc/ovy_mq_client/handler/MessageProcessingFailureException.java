package io.github.jotabrc.ovy_mq_client.handler;

public class MessageProcessingFailureException extends RuntimeException {
    public MessageProcessingFailureException(String message, String topic) {
        super("Error invoking listener %s: %s".formatted(topic, message));
    }
}
