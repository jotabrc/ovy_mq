package io.github.jotabrc.ovy_mq_client.component.listener;

import org.springframework.stereotype.Component;

@Component
public class ListenerAfterProcessingHandlerImpl implements ListenerAfterProcessingHandler {

    @Override
    public <T> void afterSuccess(T payload) {

    }

    @Override
    public <T> void afterFailure(T payload, RuntimeException throwable) {
        throw throwable;
    }
}
