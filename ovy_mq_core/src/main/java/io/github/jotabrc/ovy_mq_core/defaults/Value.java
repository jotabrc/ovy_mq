package io.github.jotabrc.ovy_mq_core.defaults;

import java.util.function.Function;

public class Value {

    // Principal
    public static final String PRINCIPAL_IS_MISSING = null;

    // PAYLOAD TYPES
    public static final String PAYLOAD_TYPE_MESSAGE_PAYLOAD = "message-payload";
    public static final String PAYLOAD_TYPE_HEALTH_STATUS = "health-status";
    public static final String PAYLOAD_TYPE_LISTENER_CONFIG = "listener-config";

    // Destination Defaults
    public static final String DESTINATION_SERVER = "ovy-server";

    // Topic for ListenerConfig
    public static final String LISTENER_CONFIG_TOPIC = "ovy-listener-config";

    // Roles
    public static final String ROLE_CONSUMER = "ovy-consumer";
    public static final String ROLE_SERVER = "ovy-server";
    public static final String ROLE_CONFIGURER = "ovy-configurer";
    public static final String ROLE_OVY = "ovy-ovy";

    // ListenerConfigHandler Key
    public static final Function<String, String> LOCK_KEY_VALUE = "key:listener:config:handler:"::concat;
}
