package io.github.jotabrc.ovy_mq_client.session;

import io.github.jotabrc.ovy_mq_client.config.SessionTypeConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SessionTypeProvider {

    private final SessionTypeConfig sessionTypeConfig;

    public SessionType get() {
        return sessionTypeConfig.getSessionType();
    }
}