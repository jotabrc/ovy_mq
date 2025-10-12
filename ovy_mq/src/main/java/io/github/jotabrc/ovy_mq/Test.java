package io.github.jotabrc.ovy_mq;

import io.github.jotabrc.ovy_mq.domain.MessagePayload;
import io.github.jotabrc.ovy_mq.domain.factory.MessageRecordFactory;
import io.github.jotabrc.ovy_mq.service.handler.executor.MessageHandlerExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static io.github.jotabrc.ovy_mq.service.handler.strategy.MessageRegistryStrategy.SAVE;

@Component
@RequiredArgsConstructor
public class Test implements CommandLineRunner {

    private final MessageHandlerExecutor messageHandlerExecutor;

    @Override
    public void run(String... args) throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        while (counter.getAndIncrement() < 100000) {
            messageHandlerExecutor.execute(SAVE, MessageRecordFactory.of(MessagePayload.builder()
                    .topic("teste")
                    .payload("String value")
                    .build()));
        }
    }
}
