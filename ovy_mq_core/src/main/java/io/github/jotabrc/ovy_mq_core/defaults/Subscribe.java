package io.github.jotabrc.ovy_mq_core.defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static io.github.jotabrc.ovy_mq_core.defaults.Mapping.*;

public class Subscribe {

    public static final String HEALTH_CHECK = WS_USER + WS_HEALTH;
    public static final String LISTENER_CONFIG_QUEUE = WS_USER + WS_CONFIG;
    public static final Function<String, String> MESSAGE_PAYLOAD_QUEUE = Subscribe::getMessagePayloadQueueValue;

    public static final Function<String, List<String>> CONSUMER_SUBSCRIPTION = topic -> new ArrayList<>(List.of(HEALTH_CHECK, MESSAGE_PAYLOAD_QUEUE.apply(topic)));
    public static final List<String> CONFIGURER_SUBSCRIPTION = List.of(HEALTH_CHECK, LISTENER_CONFIG_QUEUE);
    public static final List<String> PRODUCER_SUBSCRIPTION = List.of(HEALTH_CHECK);

    private static String getMessagePayloadQueueValue(String topic) {
        return WS_USER + WS_QUEUE + "/%s".formatted(topic);
    }
}
