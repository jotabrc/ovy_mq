package io.github.jotabrc.ovy_mq.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ListenerQueue {

    private final RedisTemplate<String, String> redisTemplate;
    private final WebSocketSessionManager sessionManager;

    public void start() {
        Flux.interval(Duration.ofMillis(100))
                .flatMap(tick -> Mono.fromCallable(() -> redisTemplate.opsForList().rightPop("message:topic")))
                .filter(Objects::nonNull)
                .subscribe(msg -> {
                    sessionManager.sendToAll(msg);
                });
    }
}
