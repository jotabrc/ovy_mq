package io.github.jotabrc.ovy_mq_core.constants;

public class Mapping {

    private Mapping() {
    }

    public static final String WS_CLIENT = "/client";
    public static final String WS_USER = "/user";
    public static final String WS_REQUEST = "/request";
    public static final String WS_QUEUE = "/queue";
    public static final String WS_SAVE = "/save";
    public static final String WS_REGISTRY = "/registry";
    public static final String WS_MESSAGE = "/message";
    public static final String WS_CONFIRM = "/confirm";
    public static final String WS_HEALTH = "/health";
    public static final String WS_LISTENER = "/listener";
    public static final String WS_CONFIG = "/config";

    public static String SEND_COMMAND_TO_SERVER = WS_REQUEST + WS_CLIENT;
}
