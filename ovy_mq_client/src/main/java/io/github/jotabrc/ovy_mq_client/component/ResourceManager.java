package io.github.jotabrc.ovy_mq_client.component;

import io.github.jotabrc.ovy_mq_client.component.initialize.registry.SessionRegistry;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ResourceManager {

    private final SessionRegistry sessionRegistry;

    @PreDestroy
    private void destroy() {
        sessionRegistry.getAll()
                .forEach((key, sessionManager) ->
                        sessionManager.destroy());
    }
}
