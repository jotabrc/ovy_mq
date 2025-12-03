package io.github.jotabrc.ovy_mq_core.domain.client;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OvyListener {
    String topic();
    int quantity() default 1;
    int max() default 3;
    int min() default 1;
    int step() default 1;
    boolean autoManage() default false;
    long processingTimeout() default 150000;
    long pollInitialDelay() default 10000;
    long pollFixedDelay() default 35000;
    long healthCheckInitialDelay() default 10000;
    long healthCheckFixedDelay() default 35000;
    long healthCheckExpirationTime() default 120000;
    int connectionMaxRetries() default 50;
    long connectionTimeout() default 150000;
    boolean useGlobalValues() default false;
}
