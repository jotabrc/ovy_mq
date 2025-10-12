package io.github.jotabrc.ovy_mq.service.handler.interfaces;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;

public interface MessageRemoveHandler extends MessageAbstractHandler {

    MessagePayload handle(MessagePayload messagePayload);
}
