package io.github.jotabrc.ovy_mq_client.test;

import io.github.jotabrc.ovy_mq_client.service.OvyListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@RequiredArgsConstructor
@Component
public class Test {

    private final TestRepo repo;

    @OvyListener(topic = "bar", quantity = 10, timeout = 35000)
    public void bar(Object object) {
        try {
            Thread.sleep(Random.from(new Random()).nextInt(100, 29000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        repo.save(TestObj.builder().objeto(object.toString()).build());
    }

    @OvyListener(topic = "foo", quantity = 10, timeout = 35000)
    public void foo(Object object) {
        try {
            Thread.sleep(Random.from(new Random()).nextInt(100, 29000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        repo.save(TestObj.builder().objeto(object.toString()).build());
    }
}
