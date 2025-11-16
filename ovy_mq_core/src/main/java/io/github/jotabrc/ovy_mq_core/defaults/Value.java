package io.github.jotabrc.ovy_mq_core.defaults;

public class Value {

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
}
