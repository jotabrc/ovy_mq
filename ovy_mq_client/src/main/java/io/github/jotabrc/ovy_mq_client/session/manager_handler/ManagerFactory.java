package io.github.jotabrc.ovy_mq_client.session.manager_handler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ManagerFactory {

    STOMP_HEALTH_CHECK,
    STOMP_LISTENER_POLL;
}
