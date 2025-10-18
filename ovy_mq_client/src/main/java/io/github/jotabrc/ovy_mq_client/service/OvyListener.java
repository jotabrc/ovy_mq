package io.github.jotabrc.ovy_mq_client.service;

import org.springframework.scheduling.annotation.Async;

import java.lang.annotation.*;

@Async
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OvyListener {
    String topic() default "";
    int replicas() default 1;
    ListenerState initialState() default ListenerState.STANDBY;
}
