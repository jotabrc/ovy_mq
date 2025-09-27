package io.github.jotabrc.ovy_mq_client.service;

import org.springframework.scheduling.annotation.Async;

import java.lang.annotation.*;

@Async
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConsumerListener {
    String value() default "";
    Class<?> payloadType();
    String consumers() default "1";
    ListenerState initialState() default ListenerState.STANDBY;
}
