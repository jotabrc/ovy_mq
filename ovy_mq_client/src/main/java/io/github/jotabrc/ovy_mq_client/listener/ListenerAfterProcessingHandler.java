package io.github.jotabrc.ovy_mq_client.listener;

public interface ListenerAfterProcessingHandler {

    <T> void afterSuccess(T payload);
    <T> void afterFailure(T payload, RuntimeException throwable) throws RuntimeException;
}
