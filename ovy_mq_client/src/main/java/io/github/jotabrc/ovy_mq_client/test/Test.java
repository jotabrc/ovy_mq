package io.github.jotabrc.ovy_mq_client.test;

import io.github.jotabrc.ovy_mq_client.service.OvyListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Test {

    private final TestRepo repo;

    @OvyListener(topic = "teste", replicas = 3)
    public void listener(Object object) {
//        try {
//            Thread.sleep(Random.from(new Random()).nextInt(1, 29000));
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        repo.save(TestObj.builder().objeto(object.toString()).build());
    }
}
