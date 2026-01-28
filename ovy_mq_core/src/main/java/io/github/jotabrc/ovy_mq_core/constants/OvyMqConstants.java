package io.github.jotabrc.ovy_mq_core.constants;

import java.util.function.Function;

public class OvyMqConstants {

    private OvyMqConstants() {}

    // HEADER
    public static final String AUTHORIZATION = "Authorization";
    public static final String ROLES = "OVY-ROLES";
    public static final String PAYLOAD_TYPE = "OVY-PAYLOAD-TYPE";
    public static final String CLIENT_ID = "OVY-CLIENT-ID";
    public static final String SUBSCRIBED_TOPIC = "OVY-SUBSCRIBED-TOPIC";
    public static final String CLIENT_TYPE = "OVY-CLIENT-TYPE";
    public static final String DESTINATION = "OVY-DESTINATION";

    // Factory
    public static final String CLIENT_OBJECT = "client-object";
    public static final String SUBSCRIPTIONS = "client-subscriptions";
    public static final String MANAGERS = "client-managers";
    public static final String CLIENT_METHOD = "client-method";
    public static final String CLIENT_BEAN_NAME = "client-bean-name";
    public static final String CLIENT_IS_AVAILABLE = "client-is-available";

    // Factory ListenerConfig
    public static final String OVY_LISTENER = "factory-ovy-listener";

    // Filter
    public static final String FILTER_SERVLET_REQUEST = "filter-servlet-request";
    public static final String FILTER_SUBJECT = "filter-subject";
    public static final String FILTER_ROLES = "filter-roles";

    // PAYLOAD TYPES
    public static final String PAYLOAD_TYPE_MESSAGE_PAYLOAD = "message-payload";
    public static final String PAYLOAD_TYPE_HEALTH_STATUS = "health-status";
    public static final String PAYLOAD_TYPE_LISTENER_CONFIG = "listener-config";

    // Destination Defaults
    public static final String DESTINATION_SERVER = "ovy-server";

    // Roles
    public static final String ROLE_CONSUMER = "ovy-consumer";
    public static final String ROLE_SERVER = "-NO-TOPIC-ovy-server";
    public static final String ROLE_CONFIGURER = "ovy-configurer";
    public static final String ROLE_OVY = "ovy-ovy";

    // ListenerConfigHandler Lock key
    public static final Function<String, String> LOCK_KEY_VALUE = "key:listener:config:handler:"::concat;

    // Objects
    public static final String OBJECT_HEALTH_STATUS = "OBJECT-HEALTH-STATUS";
    public static final String OBJECT_LISTENER_CONFIG = "OBJECT-LISTENER-CONFIG";
    public static final String OBJECT_REAPER_TIME_MS = "OBJECT-REAPER_MESSAGE_PAYLOAD-TIME-MS";
    public static final String OBJECT_MESSAGE_PAYLOAD = "OBJECT-MESSAGE-PAYLOAD";
    public static final String OBJECT_CLIENT = "OBJECT-CLIENT";
}
