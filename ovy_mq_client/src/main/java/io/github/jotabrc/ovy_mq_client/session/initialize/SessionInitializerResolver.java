package io.github.jotabrc.ovy_mq_client.session.initialize;

import io.github.jotabrc.ovy_mq_client.session.SessionType;
import io.github.jotabrc.ovy_mq_client.session.SessionTypeProvider;
import io.github.jotabrc.ovy_mq_client.session.initialize.interfaces.ClientHandlerInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SessionInitializerResolver {

    private final SessionTypeProvider sessionTypeProvider;
    private final Map<SessionType, ClientHandlerInitializer> initializers;

    @Autowired
    public SessionInitializerResolver(SessionTypeProvider sessionTypeProvider,
                                      List<ClientHandlerInitializer> initializers) {
        this.sessionTypeProvider = sessionTypeProvider;
        this.initializers = initializers.stream()
                .collect(Collectors.toMap(
                        ClientHandlerInitializer::supports,
                        initializer -> initializer
                ));
    }

    public Optional<ClientHandlerInitializer> get() {
        return Optional.ofNullable(initializers.get(sessionTypeProvider.get()));
    }
}
