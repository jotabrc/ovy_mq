package io.github.jotabrc.ovy_mq_client.test;

import io.github.jotabrc.ovy_mq_client.producer.interfaces.OvyProducer;
import io.github.jotabrc.ovy_mq_core.domain.payload.MessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProducerTest implements CommandLineRunner {

    private final OvyProducer stompOvyProducer;

    @Override
    public void run(String... args) throws Exception {
        Thread.sleep(60000);
        execute();
    }

    public void execute() {
        AtomicInteger counter = new AtomicInteger(0);
        while (counter.getAndIncrement() < 1000) {
            log.info("saving message.......................");
            stompOvyProducer.send(MessagePayload.builder()
                            .topic("bar")
                            .payload("" + counter.get())
                            .build());
        }

        counter = new AtomicInteger(0);
        while (counter.getAndIncrement() < 1000) {
            log.info("saving message.......................");
            stompOvyProducer.send(MessagePayload.builder()
                    .topic("foo")
                    .payload("" + counter.get())
                    .build());
        }
    }
}
