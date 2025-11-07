package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.service.components.interfaces.OvyHeaders;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

@Component
public class StompHeadersFactory implements OvyHeaders<StompHeaders> {

    @Override
    public StompHeaders createDefault(String destination, String topic) {
        StompHeaders headers = new StompHeaders();
        headers.setDestination(destination);
        headers.add(Key.HEADER_TOPIC, topic);
        return headers;
    }

    @Override
    public Class<?> supports() {
        return StompHeaders.class;
    }
}
