package io.github.jotabrc.ovy_mq_core.defaults;

public class Key {

    // HEADER's
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_AUTHORIZATION_TYPE = "OVY-AUTHORIZATION-TYPE";
    public static final String HEADER_ROLE = "OVY-ROLES";
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

    // Factory ClientConfig
    public static final String FACTORY_REPLICA_QUANTITY = "replica-quantity";
    public static final String FACTORY_REPLICA_MAX = "replica-max";
    public static final String FACTORY_REPLICA_MIN = "replica-min";
    public static final String FACTORY_REPLICA_STEP = "replica-step";
    public static final String FACTORY_REPLICA_AUTO_MANAGE = "replica-auto-manage";
    public static final String FACTORY_PROCESSING_TIMEOUT = "replica-processing-timeout";
    public static final String FACTORY_CLIENT_CONFIG_POLL_INITIAL_DELAY = "client-config-poll-initial-delay";
    public static final String FACTORY_CLIENT_CONFIG_POLL_FIXED_DELAY = "client-config-poll-fixed-delay";
    public static final String FACTORY_CLIENT_CONFIG_HEALTH_CHECK_INITIAL_DELAY = "client-config-health-check-initial-delay";
    public static final String FACTORY_CLIENT_CONFIG_HEALTH_CHECK_FIXED_DELAY = "client-config-health-check-fixed-delay";
    public static final String FACTORY_CLIENT_CONFIG_HEALTH_CHECK_EXPIRATION_TIME = "client-config-health-check-expiration-time";
    public static final String FACTORY_CLIENT_CONFIG_CONNECTION_MAX_RETRIES = "client-config-connection-max-retires";
    public static final String FACTORY_CLIENT_CONFIG_CONNECTION_TIMEOUT = "client-config-connection-timeout";
    public static final String FACTORY_CLIENT_CONFIG_USE_GLOBAL_VALUES = "client-config-use-global-values";

    // Filter
    public static final String FILTER_SERVLET_REQUEST = "filter-servlet-request";
    public static final String FILTER_SERVLET_RESPONSE = "filter-servlet-response";
    public static final String FILTER_CHAIN = "filter-chain";
    public static final String FILTER_SUBJECT = "filter-subject";
    public static final String FILTER_ROLES = "filter-roles";
    public static final String FILTER_AUTHENTICATION = "filter-authentication";
}
