package io.github.jotabrc.ovy_mq.service;

import io.github.jotabrc.ovy_mq.domain.Client;
import io.github.jotabrc.ovy_mq.domain.MessagePayload;

public interface QueueProcessor {

    void save(MessagePayload messagePayload);
    void send(Client client);
}
