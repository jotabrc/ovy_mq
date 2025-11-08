package io.github.jotabrc.ovy_mq_client.service.components;

import io.github.jotabrc.ovy_mq_client.service.components.interfaces.OvyHeaderFactory;
import io.github.jotabrc.ovy_mq_core.defaults.Key;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.stereotype.Component;

@Component
public class StompHeaderFactory implements OvyHeaderFactory<StompHeaders> {

    @Override
    public StompHeaders createDefault(String destination, String topic) {
        StompHeaders headers = new StompHeaders();
        headers.setDestination(destination);
        headers.add(Key.HEADER_TOPIC, topic);
        return headers;
    }

    @Override
    public Class<StompHeaders> supports() {
        return StompHeaders.class;
    }
}
