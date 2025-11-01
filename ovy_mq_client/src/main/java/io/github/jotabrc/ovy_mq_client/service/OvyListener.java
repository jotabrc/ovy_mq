package io.github.jotabrc.ovy_mq_client.service;

import org.springframework.scheduling.annotation.Async;

import java.lang.annotation.*;

@Async
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OvyListener {
    String topic() default "";
    int maxReplicas() default 1;
    int minReplicas() default 0;
    int stepReplicas() default 1;
    boolean autoManageReplicas() default false;
    // TODO:
    /*
    1- keep state of client replicas for step up/down with configuration
    2- clients subscription to broadcast with each with it's own topic
        broadcast will be sent to all clients with topic X
    3- new Payload/type configuration
    4- new configuration handler
    5- (Server) new Mapping for configuration (HTTP) for runtime usage
     */
}
