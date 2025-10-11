package io.github.jotabrc.ovy_mq.service;

public class BrokerMapping {

    private BrokerMapping() {}

    public static final String DEFAULT_PREFIX = "/request";
    public static final String SAVE_MESSAGE_RECEIVED = "/save";
    public static final String CLIENT_REGISTRATION = "/registry";
    public static final String SEND_MESSAGE_TO_CONSUMER = "/queue";
    public static final String SEND_CONFIG_TO_CONSUMER = "/send-config";
    public static final String RECEIVE_CONFIG_FROM_CONSUMER = "/receive-config";
    public static final String RECEIVE_MESSAGE_REQUEST_FROM_CONSUMER = "/message";
    public static final String RECEIVE_MESSAGE_PROCESSING_SUCCESS_CONFIRMATION = "/message/confirm";
}
