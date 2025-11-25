package io.github.jotabrc.ovy_mq_client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    public static final String LISTENER_EXECUTOR = "listenerExecutor";
    public static final String SCHEDULED_EXECUTOR = "scheduledExecutor";

    /*
    TODO
    Executor pool properties configuration
     */

    @Bean(name = SCHEDULED_EXECUTOR)
    public ScheduledExecutorService scheduledExecutor() {
        return new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = new Thread(r, "ScheduledExecutor");
            thread.setDaemon(true);
            return thread;
        });
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
