package io.github.jotabrc.ovy_mq_core.defaults;

import java.util.HashMap;
import java.util.Map;

public class Header {

    public static TriFunction<String, String, String, Map<String, String>> DEFAULT = Header::create;

    private static Map<String, String> create(String destination, String topic, String clientType) {
        return new HashMap<>(Map.of(Key.FACTORY_DESTINATION, destination,
                Key.HEADER_TOPIC, topic,
                Key.HEADER_CLIENT_TYPE, clientType));
    }
}
