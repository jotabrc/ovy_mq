package io.github.jotabrc.ovy_mq_client.service.handler.executor;

import io.github.jotabrc.ovy_mq_client.domain.HealthStatus;
import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientHealthCheckHandler;
import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.ClientMessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@RequiredArgsConstructor
@Component
public class ClientMessageHandlerExecutor {

    private final ClientHealthCheckHandler clientHealthCheckHandler;
    private final ClientMessageHandler clientMessageHandler;

    public void execute(StompHeaders headers, Object object, String clientId, Type type) {
        if (type.getTypeName().equals(MessagePayload.class.getTypeName())) {
            String destination = headers.getDestination();
            if (destination != null) {
                String topic = destination.substring("/user/queue/".length());
                MessagePayload messagePayload = (MessagePayload) object;
                messagePayload.setTopic(topic);
                clientMessageHandler.handle(clientId, topic, messagePayload);
            }
        } else if (type.getTypeName().equals(HealthStatus.class.getTypeName())) {
            HealthStatus healthStatus = (HealthStatus) object;
            clientHealthCheckHandler.handle(clientId, healthStatus);
        }
    }
}
