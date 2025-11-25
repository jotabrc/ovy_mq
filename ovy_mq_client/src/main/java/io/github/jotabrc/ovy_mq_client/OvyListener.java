package io.github.jotabrc.ovy_mq_client;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OvyListener {
    String topic();
    int quantity() default 1;
    int max() default 3;
    int min() default 0;
    int step() default 1;
    boolean autoManage() default false;
    long timeout() default 10000;
    long pollInitialDelay() default 10000;
    long pollFixedDelay() default 35000;
}
