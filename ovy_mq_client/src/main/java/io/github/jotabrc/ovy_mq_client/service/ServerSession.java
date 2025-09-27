package io.github.jotabrc.ovy_mq_client.service;

import io.github.jotabrc.ovy_mq_client.domain.MessagePayload;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ServerSession {

    private final ClientSession session;

    public void notifyResult(MessagePayload message) {
        session.getSession().send("/queue/notify-and-request", message);
    }

    public void requestMessage(String topic) {
        session.getSession().send("/queue/request/message", topic);
    }
}
