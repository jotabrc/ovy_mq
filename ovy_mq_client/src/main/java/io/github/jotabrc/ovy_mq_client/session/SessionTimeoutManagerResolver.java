package io.github.jotabrc.ovy_mq_client.session;

import io.github.jotabrc.ovy_mq_client.session.interfaces.SessionTimeoutManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SessionTimeoutManagerResolver {

    private final Map<SessionType, SessionTimeoutManager> managers;

    @Autowired
    public SessionTimeoutManagerResolver(List<SessionTimeoutManager> managers) {
        this.managers = managers.stream()
                .collect(Collectors.toMap(
                        SessionTimeoutManager::supports,
                        manager -> manager
                ));
    }

    public Optional<SessionTimeoutManager> get(SessionType sessionType) {
        return Optional.ofNullable(managers.get(sessionType));
    }
}
