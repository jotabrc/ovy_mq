package io.github.jotabrc.ovy_mq_client;

import io.github.jotabrc.ovy_mq_client.service.OvyListener;
import org.springframework.stereotype.Component;

@Component
public class Test {

    @OvyListener(topic = "teste", replicas = 1)
    public void listener(Object object) {
        return;
    }
}
