package io.github.jotabrc.ovy_mq_client.handler;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }
}
