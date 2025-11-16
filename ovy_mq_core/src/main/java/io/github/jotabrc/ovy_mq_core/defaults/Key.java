package io.github.jotabrc.ovy_mq_core.defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class Key {

    // HEADER's
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_ROLES = "OVY-ROLES";
    public static final String HEADER_PAYLOAD_TYPE = "OVY-PAYLOAD-TYPE";
    public static final String HEADER_CLIENT_ID = "OVY-CLIENT-ID";
    public static final String HEADER_TOPIC = "OVY-SUBSCRIBED-TOPIC";
    public static final String HEADER_CLIENT_TYPE = "OVY-CLIENT-TYPE";
    public static final String HEADER_DESTINATION = "OVY-DESTINATION";

    // Factory
    public static final String FACTORY_TYPE = "factory-type";
    public static final String FACTORY_CLIENT_OBJECT = "client-object";
    public static final String FACTORY_SUBSCRIPTIONS = "client-subscriptions";
    public static final String FACTORY_CLIENT_METHOD = "client-method";
    public static final String FACTORY_CLIENT_BEAN_NAME = "client-bean-name";
    public static final String FACTORY_CLIENT_TIMEOUT = "client-timeout";
    public static final String FACTORY_CLIENT_IS_AVAILABLE = "client-is-available";

    // Factory replica
    public static final String FACTORY_REPLICA_QUANTITY = "replica-quantity";
    public static final String FACTORY_REPLICA_MAX = "replica-max";
    public static final String FACTORY_REPLICA_MIN = "replica-min";
    public static final String FACTORY_REPLICA_STEP = "replica-step";
    public static final String FACTORY_REPLICA_AUTO_MANAGE = "replica-auto-manage";

    public static <R> R extract(Map<String, Object> map, String key, Class<R> returningType) {
        Object value = map.remove(key);
        return nonNull(value)
                ? returningType.cast(value)
                : null;
    }

    public static <R> List<R> extractToList(Map<String, Object> map, String key, Class<R> returningType) {
        Object value = map.remove(key);
        if (value instanceof List<?> list) {
            return list.stream()
                    .map(returningType::cast)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static <R> Map<String, R> convert(Map<String, Object> map, Class<R> returningType) {
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> returningType.cast(entry.getValue())));
    }
}
