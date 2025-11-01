package io.github.jotabrc.ovy_mq_client.domain.factory;

import io.github.jotabrc.ovy_mq_client.domain.defaults.Key;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class StompHeaderFactory {

    private StompHeaderFactory() {}

    public static StompHeaders get(String topic, String destination) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination(destination);
        stompHeaders.add(Key.HEADER_TOPIC, topic);
        return stompHeaders;
    }
}
