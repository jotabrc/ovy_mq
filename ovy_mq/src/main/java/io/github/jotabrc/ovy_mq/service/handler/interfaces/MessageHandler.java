package io.github.jotabrc.ovy_mq.service.handler.interfaces;

import io.github.jotabrc.ovy_mq.domain.MessageRecord;

public interface MessageHandler {

    MessageRecord handle(MessageRecord messageRecord);
}
