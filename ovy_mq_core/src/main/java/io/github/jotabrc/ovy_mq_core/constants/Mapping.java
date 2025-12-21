package io.github.jotabrc.ovy_mq_core.constants;

public class Mapping {

    private Mapping() {
    }

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

    public static String CONFIRM_PAYLOAD_RECEIVED = WS_REQUEST + WS_MESSAGE + WS_CONFIRM;
    public static String REQUEST_MESSAGE = WS_REQUEST + WS_MESSAGE;
    public static String REQUEST_HEALTH_CHECK = WS_REQUEST + WS_HEALTH;
    public static String SAVE_MESSAGE = WS_REQUEST + WS_SAVE;
}
