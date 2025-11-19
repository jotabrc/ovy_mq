package io.github.jotabrc.ovy_mq_core.defaults;

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
}
