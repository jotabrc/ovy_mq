package io.github.jotabrc.ovy_mq.test;

import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcher;
import io.github.jotabrc.ovy_mq.service.handler.PayloadDispatcherCommand;
import io.github.jotabrc.ovy_mq_core.domain.MessagePayload;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class Test implements CommandLineRunner {

    private final PayloadDispatcher payloadDispatcher;

    @Override
    public void run(String... args) throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        while (counter.getAndIncrement() < 1000000) {
            payloadDispatcher.execute(MessagePayload.builder()
                    .topic("teste")
                    .payload("" + counter.get())
                    .build(),
                    PayloadDispatcherCommand.SAVE);
        }
    }
}
