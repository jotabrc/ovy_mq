package io.github.jotabrc.ovy_mq_client.service.components.handler;

import io.github.jotabrc.ovy_mq_client.service.components.AbstractFactoryResolver;
import io.github.jotabrc.ovy_mq_client.service.registry.SessionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConsumerStompSessionHandler extends AbstractStompSessionHandler {

    public ConsumerStompSessionHandler(PayloadHandlerDispatcher payloadHandlerDispatcher, AbstractFactoryResolver abstractFactoryResolver, PayloadConfirmationHandlerDispatcher payloadConfirmationHandlerDispatcher, SessionRegistry sessionRegistry, WebSocketStompClient webSocketStompClient) {
        super(payloadHandlerDispatcher, abstractFactoryResolver, payloadConfirmationHandlerDispatcher, sessionRegistry, webSocketStompClient);
    }
}
