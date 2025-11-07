package io.github.jotabrc.ovy_mq_client.service.handler.payload;

import io.github.jotabrc.ovy_mq_client.service.handler.interfaces.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PayloadConfirmationDispatcher {

    public void acknowledge(SessionManager session) {

    }
}
