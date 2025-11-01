package io.github.jotabrc.ovy_mq.domain.defaults;

public class Key {

    // PAYLOAD TYPES
    public static final String PAYLOAD_TYPE_MESSAGE_PAYLOAD = "message-payload";
    public static final String PAYLOAD_TYPE_HEALTH_STATUS = "health-status";

    // HEADER's
    public static final String HEADER_PAYLOAD_TYPE = "X-PAYLOAD-TYPE";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CLIEND_ID = "X-CLIENT_ID";
    public static final String HEADER_TOPIC = "X-SUBSCRIBED-TOPIC";
}
