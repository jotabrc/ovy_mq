package io.github.jotabrc.ovy_mq_core.defaults;

public class Key {

    // PAYLOAD TYPES
    public static final String PAYLOAD_TYPE_MESSAGE_PAYLOAD = "message-payload";
    public static final String PAYLOAD_TYPE_HEALTH_STATUS = "health-status";
    public static final String PAYLOAD_TYPE_LISTENER_CONFIG = "listener-config";

    // HEADER's
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_PAYLOAD_TYPE = "X-PAYLOAD-TYPE";
    public static final String HEADER_CLIEND_ID = "X-CLIENT_ID";
    public static final String HEADER_TOPIC = "X-SUBSCRIBED-TOPIC";
    public static final String HEADER_CLIENT_TYPE = "client-type";
    public static final String HEADER_CLIENT_TYPE_PRODUCER = "producer";
    public static final String HEADER_CLIENT_TYPE_CONSUMER = "consumer";
    public static final String HEADER_CLIENT_TYPE_CONFIGURER = "configurer";

    // Factory
    public static final String FACTORY_DESTINATION = "destination";
    public static final String FACTORY_CLIENT = "client-object";
}
