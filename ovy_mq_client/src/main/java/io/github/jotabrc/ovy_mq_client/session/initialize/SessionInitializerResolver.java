package io.github.jotabrc.ovy_mq_client.session.initialize;

import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_client.session.SessionTypeProvider;
import io.github.jotabrc.ovy_mq_client.session.initialize.interfaces.SessionInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SessionInitializerResolver {

    private final SessionTypeProvider sessionTypeProvider;
    private final Map<SessionType, SessionInitializer> initializers;

    @Autowired
    public SessionInitializerResolver(SessionTypeProvider sessionTypeProvider,
                                      List<SessionInitializer> initializers) {
        this.sessionTypeProvider = sessionTypeProvider;
        this.initializers = initializers.stream()
                .collect(Collectors.toMap(
                        SessionInitializer::supports,
                        initializer -> initializer
                ));
    }

    public Optional<SessionInitializer> get() {
        return Optional.ofNullable(initializers.get(sessionTypeProvider.get()));
    }
}
