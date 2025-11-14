package io.github.jotabrc.ovy_mq_client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig {

    public static final String SMALL_POOL_EXECUTOR = "smallPoolExecutor";
    public static final String LISTENER_EXECUTOR = "listenerExecutor";

    /*
    TODO
    Executor pool properties configuration
     */

    @Bean(name = SMALL_POOL_EXECUTOR)
    public Executor smallPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Small-ThreadPool-");
        executor.initialize();
        return executor;
    }

    @Bean(name = LISTENER_EXECUTOR)
    public Executor listenerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("MessagePayloadHandler-ThreadPool-");
        executor.initialize();
        return executor;
    }
}
