package io.github.jotabrc.ovy_mq_core.domain.action;

public enum OvyCommand {

    SAVE_MESSAGE_PAYLOAD,
    REQUEST_MESSAGE_PAYLOAD,
    REMOVE_MESSAGE_PAYLOAD,
    REAPER_MESSAGE_PAYLOAD,
    REQUEST_HEALTH_CHECK,
    SEND_LISTENER_CONFIG;
}
