package io.github.jotabrc.ovy_mq.handler;

public class JsonToMessageException extends RuntimeException {
    public JsonToMessageException(String message) {
        super("Error while converting json to message from topic %s".formatted(topic));
    }
}
