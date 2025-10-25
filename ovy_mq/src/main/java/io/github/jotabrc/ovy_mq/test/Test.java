package io.github.jotabrc.ovy_mq.test;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.service.handler.PayloadExecutor;
import io.github.jotabrc.ovy_mq.service.handler.PayloadHandlerCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class Test implements CommandLineRunner {

    private final PayloadExecutor payloadExecutor;

    @Override
    public void run(String... args) throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        while (counter.getAndIncrement() < 1000000) {
            payloadExecutor.execute(MessagePayload.builder()
                    .topic("teste")
                    .payload("" + counter.get())
                    .build(),
                    PayloadHandlerCommand.SAVE);
        }
    }
}
