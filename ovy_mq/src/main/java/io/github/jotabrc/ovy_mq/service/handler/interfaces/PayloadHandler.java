package io.github.jotabrc.ovy_mq.service.handler.interfaces;

import io.github.jotabrc.ovy_mq_core.domain.action.OvyAction;

public interface PayloadHandler {

    void handle(OvyAction ovyAction);
    io.github.jotabrc.ovy_mq_core.domain.action.OvyCommand command();
}
