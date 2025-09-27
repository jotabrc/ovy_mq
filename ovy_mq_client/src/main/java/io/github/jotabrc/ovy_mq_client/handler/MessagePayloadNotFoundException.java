package io.github.jotabrc.ovy_mq_client.handler;

public class MessagePayloadNotFoundException extends RuntimeException {
    public MessagePayloadNotFoundException(String message) {
        super(message);
    }
}
