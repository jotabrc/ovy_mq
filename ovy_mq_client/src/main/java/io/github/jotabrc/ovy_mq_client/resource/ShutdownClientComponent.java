package io.github.jotabrc.ovy_mq_client.resource;

import io.github.jotabrc.ovy_mq_client.resource.shutdown.ShutdownUtil;
import io.github.jotabrc.ovy_mq_client.registry.SessionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShutdownClientComponent extends ShutdownUtil {

    public ShutdownClientComponent(SessionRegistry sessionRegistry) {
        super(sessionRegistry);
    }
}
