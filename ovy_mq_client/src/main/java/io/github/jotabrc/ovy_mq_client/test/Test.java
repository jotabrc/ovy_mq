package io.github.jotabrc.ovy_mq_client.test;

import io.github.jotabrc.ovy_mq_client.service.OvyListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Test {

    private final TestRepo repo;

    @OvyListener(topic = "teste")
    public void listener(Object object) {
        repo.save(TestObj.builder().objeto(object.toString()).build());
    }
}
