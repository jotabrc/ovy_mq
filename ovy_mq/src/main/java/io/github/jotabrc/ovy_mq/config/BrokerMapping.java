package io.github.jotabrc.ovy_mq.config;

public class BrokerMapping {

    private BrokerMapping() {}

    public static final String DEFAULT_PREFIX = "/request";
    public static final String SENDER_MESSAGE_TO_CLIENT = "/queue";
    public static final String SENDER_CONFIGURATION_TO_CLIENT = "/send-config";

    public static final String SAVE_MESSAGE = "/save";
    public static final String RECEIVER_REGISTRATION = "/registry";
    public static final String CONFIGURE = "/receive-config";
    public static final String MESSAGE_REQUEST = "/message";
    public static final String MESSAGE_PROCESSED = "/message/confirm";
    public static final String HEALTH_CHECK = "/health";
}
