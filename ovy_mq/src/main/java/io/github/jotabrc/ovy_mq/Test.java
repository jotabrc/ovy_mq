package io.github.jotabrc.ovy_mq;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.service.handler.interfaces.MessageSaveHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class Test implements CommandLineRunner {

    private final MessageSaveHandler messageSaveHandler;

    @Override
    public void run(String... args) throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        while (counter.getAndIncrement() < 100000) {
            messageSaveHandler.handle(MessagePayload.builder()
                    .topic("teste")
                    .payload("String value")
                    .build());
        }
    }
}
